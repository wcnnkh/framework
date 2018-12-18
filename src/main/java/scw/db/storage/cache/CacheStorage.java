package scw.db.storage.cache;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.common.Iterator;
import scw.common.Logger;
import scw.common.Multitask;
import scw.common.transaction.AbstractTransaction;
import scw.common.transaction.Transaction;
import scw.common.transaction.TransactionCollection;
import scw.common.utils.ClassUtils;
import scw.db.AbstractDB;
import scw.db.OperationBean;
import scw.db.TransactionContext;
import scw.db.annoation.Table;
import scw.db.result.Result;
import scw.db.sql.SQL;
import scw.db.storage.Storage;
import scw.memcached.Memcached;
import scw.mq.MQ;
import scw.mq.MemcachedMQ;
import scw.mq.RedisMQ;
import scw.redis.Redis;

public final class CacheStorage implements Storage {
	private final Map<String, CacheConfig> cacheConfigMap = new HashMap<String, CacheConfig>();
	private final Cache cache;
	private final AbstractDB db;
	// 缓存是否自动提交，如果为false就是要等transaction.commit()的时候再提交
	// 默认参与事务，应该以保证数据完整性为主
	private boolean cacheAutoCommit = false;
	private final MQ<Collection<OperationBean>> mq;
	private CacheConfig defaultCacheConfig;

	public CacheStorage(AbstractDB db, Memcached memcached, String queueKey) {
		this.db = db;
		this.cache = new MemcachedCache(memcached);
		this.mq = new MemcachedMQ<Collection<OperationBean>>(memcached,
				queueKey);
		this.mq.consumer(new CacheAsyncConsumer(this));
		this.mq.start();
	}

	public void setDefaultCacheConfig(CacheConfig defaultCacheConfig) {
		this.defaultCacheConfig = defaultCacheConfig;
	}

	public CacheStorage(AbstractDB db, Redis redis, String queueKey) {
		this(db, redis, queueKey, Charset.forName("UTF-8"));
	}

	public CacheStorage(AbstractDB db, Redis redis, String queueKey,
			Charset charset) {
		this.db = db;
		this.cache = new RedisCache(redis, charset);
		this.mq = new RedisMQ<Collection<OperationBean>>(redis, queueKey,
				charset);
		this.mq.consumer(new CacheAsyncConsumer(this));
		this.mq.start();
	}

	public AbstractDB getDB() {
		return db;
	}

	public Cache getCache() {
		return cache;
	}

	public boolean isCacheAutoCommit() {
		return cacheAutoCommit;
	}

	public void setCacheAutoCommit(boolean cacheAutoCommit) {
		this.cacheAutoCommit = cacheAutoCommit;
	}

	public MQ<Collection<OperationBean>> getMq() {
		return mq;
	}

	public void config(CacheType cacheType, int exp, boolean isAsync,
			Class<?>... tableClass) {
		config(new CacheConfig(cacheType, exp, isAsync), tableClass);
	}

	public void config(CacheType cacheType, boolean isAsync,
			Class<?>... tableClass) {
		config(new CacheConfig(cacheType, CacheConfig.DATA_DEFAULT_EXP_TIME,
				isAsync), tableClass);
	}

	public void config(CacheConfig config, Class<?>... tableClass) {
		for (Class<?> t : tableClass) {
			Table table = t.getAnnotation(Table.class);
			if (table == null) {
				continue;
			}

			String name = ClassUtils.getProxyRealClassName(t);
			cacheConfigMap.put(name, config);
		}
	}

	public void init() {
		if (!cacheConfigMap.isEmpty()) {
			Multitask multitask = new Multitask();
			for (Entry<String, CacheConfig> entry : cacheConfigMap.entrySet()) {
				switch (entry.getValue().getCacheType()) {
				case no:
				case lazy:
					break;
				default:
					try {
						multitask.add(new LoadingThread(this, ClassUtils
								.forName(entry.getKey())));
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					break;
				}
			}

			try {
				multitask.executeAndAwait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void destroy() {
		mq.destroy();
	}

	protected CacheConfig getCacheConfig(Class<?> tableClass) {
		CacheConfig cacheInfo = cacheConfigMap.get(ClassUtils
				.getProxyRealClassName(tableClass));
		if (cacheInfo == null) {
			return defaultCacheConfig == null ? CacheConfig.DEFAULT_CACHE_CONFIG
					: defaultCacheConfig;
		}
		return cacheInfo;
	}

	public <T> T getById(Class<T> type, Object... params) {
		T t = null;
		CacheConfig cacheInfo = getCacheConfig(type);
		try {
			switch (cacheInfo.getCacheType()) {
			case lazy:
				t = cache.getById(getDB(), false, cacheInfo.getExp(), type,
						params);
				break;
			case keys:
				t = cache.getById(getDB(), true, cacheInfo.getExp(), type,
						params);
			case full:
				t = cache.getById(type, params);
			default:
				t = getDB().getByIdFromDB(type, null, params);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		CacheConfig cacheInfo = getCacheConfig(type);
		switch (cacheInfo.getCacheType()) {
		case full:
			try {
				return cache.getByIdList(getDB(), type, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		default:
			return getDB().getByIdListFromDB(type, null, params);
		}
	}

	protected void loadCache(final Class<?> tableClass) {
		final CacheConfig cacheInfo = getCacheConfig(tableClass);
		getDB().iterator(tableClass, new Iterator<Result>() {

			public void iterator(Result data) {
				Object bean = data.getObject(tableClass);
				try {
					switch (cacheInfo.getCacheType()) {
					case full:
						cache.loadFull(bean);
						break;
					case keys:
						cache.loadKey(bean);
					default:
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void op(Collection<OperationBean> operationBeans) {
		if (operationBeans == null) {
			return;
		}

		List<OperationBean> asyncList = null;// 异步列表
		List<SQL> synchronizationList = null;// 同步列表
		TransactionCollection cacheTransaction = null;
		for (OperationBean operationBean : operationBeans) {
			if (operationBean == null || operationBean.getBean() == null) {
				continue;
			}

			Transaction transaction = null;
			CacheConfig cacheConfig = getCacheConfig(operationBean.getBean()
					.getClass());
			try {
				switch (cacheConfig.getCacheType()) {
				case lazy:
					transaction = cache.opHotspot(operationBean,
							cacheConfig.getExp(), false);
					break;
				case keys:
					transaction = cache.opHotspot(operationBean,
							cacheConfig.getExp(), true);
					break;
				case full:
					transaction = cache.opByFull(operationBean);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (transaction != null) {
				if (cacheTransaction == null) {
					cacheTransaction = new TransactionCollection(
							operationBeans.size());
				}
				cacheTransaction.add(transaction);
			}

			if (cacheConfig.isAsync()) {
				if (asyncList == null) {
					asyncList = new ArrayList<OperationBean>(
							operationBeans.size());
				}

				asyncList.add(operationBean);
			} else {
				if (synchronizationList == null) {
					synchronizationList = new ArrayList<SQL>(
							operationBeans.size());
				}

				synchronizationList.add(operationBean.getSql(getDB()
						.getSqlFormat()));
			}
		}

		if (cacheAutoCommit) {
			if (cacheTransaction != null) {
				AbstractTransaction.transaction(cacheTransaction);
			}

			if (synchronizationList != null) {
				TransactionContext.getInstance().execute(getDB(),
						synchronizationList);
			}
		} else {
			TransactionContext.getInstance().execute(getDB(),
					synchronizationList, cacheTransaction);
		}

		if (asyncList != null) {
			mq.push(asyncList);
		}
	}
}

class LoadingThread implements Runnable {
	private final CacheStorage cacheStorage;
	private final Class<?> tableClass;

	public LoadingThread(CacheStorage cacheStorage, Class<?> tableClass) {
		this.tableClass = tableClass;
		this.cacheStorage = cacheStorage;
	}

	public void run() {
		final String name = ClassUtils.getProxyRealClassName(tableClass);
		Logger.info("RedisHotSpotCacheStorage", "loading [" + name
				+ "] keys to cache");
		cacheStorage.loadCache(tableClass);
		Logger.info("RedisHotSpotCacheStorage", "loading [" + name
				+ "] keys to cache success");
	}
}
