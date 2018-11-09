package shuchaowen.core.db.storage.cache;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.OperationBean;
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.PrimaryKeyValue;
import shuchaowen.core.db.TransactionContext;
import shuchaowen.core.db.annoation.Table;
import shuchaowen.core.db.result.Result;
import shuchaowen.core.db.result.ResultIterator;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.storage.Storage;
import shuchaowen.core.transaction.AbstractTransaction;
import shuchaowen.core.transaction.Transaction;
import shuchaowen.core.transaction.TransactionCollection;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.Logger;
import shuchaowen.core.util.Multitask;
import shuchaowen.core.util.XTime;
import shuchaowen.memcached.Memcached;
import shuchaowen.mq.MQ;
import shuchaowen.mq.MemcachedMQ;
import shuchaowen.mq.RedisMQ;
import shuchaowen.redis.Redis;

public final class CacheStorage implements Storage {
	private static final int DATA_DEFAULT_EXP_TIME = 7 * ((int) XTime.ONE_DAY / 1000);
	private static final CacheConfig DEFAULT_CACHE_CONFIG = new CacheConfig(CacheType.lazy, DATA_DEFAULT_EXP_TIME,
			false);
	private final Map<String, CacheConfig> cacheConfigMap = new HashMap<String, CacheConfig>();
	private final Cache cache;
	private final AbstractDB db;
	// 缓存是否自动提交，如果为false就是要等transaction.commit()的时候再提交
	// 默认参与事务，应该以保证数据完整性为主
	private boolean cacheAutoCommit = false;
	private final MQ<Collection<OperationBean>> mq;

	public CacheStorage(AbstractDB db, Memcached memcached, String queueKey) {
		this.db = db;
		this.cache = new MemcachedCache(memcached);
		this.mq = new MemcachedMQ<Collection<OperationBean>>(memcached, queueKey);
		this.mq.consumer(new CacheAsyncConsumer(this));
		this.mq.start();
	}

	public CacheStorage(AbstractDB db, Redis redis, String queueKey) {
		this(db, redis, queueKey, Charset.forName("UTF-8"));
	}

	public CacheStorage(AbstractDB db, Redis redis, String queueKey, Charset charset) {
		this.db = db;
		this.cache = new RedisCache(redis, charset);
		this.mq = new RedisMQ<Collection<OperationBean>>(redis, queueKey, charset);
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

	public void config(CacheType cacheType, int exp, boolean isAsync, Class<?>... tableClass) {
		config(new CacheConfig(cacheType, exp, isAsync), tableClass);
	}

	public void config(CacheType cacheType, boolean isAsync, Class<?>... tableClass) {
		config(new CacheConfig(cacheType, DATA_DEFAULT_EXP_TIME, isAsync), tableClass);
	}

	public void config(CacheConfig config, Class<?>... tableClass) {
		for (Class<?> t : tableClass) {
			Table table = t.getAnnotation(Table.class);
			if (table == null) {
				continue;
			}

			String name = ClassUtils.getCGLIBRealClassName(t);
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
						multitask.add(new LoadingThread(this, ClassUtils.forName(entry.getKey())));
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

	protected CacheConfig getCacheConfig(Class<?> tableClass) {
		CacheConfig cacheInfo = cacheConfigMap.get(ClassUtils.getCGLIBRealClassName(tableClass));
		return cacheInfo == null ? DEFAULT_CACHE_CONFIG : cacheInfo;
	}

	public <T> T getById(Class<T> type, Object... params) {
		T t = null;
		CacheConfig cacheInfo = getCacheConfig(type);
		try {
			switch (cacheInfo.getCacheType()) {
			case lazy:
				t = cache.getById(getDB(), false, cacheInfo.getExp(), type, params);
				break;
			case keys:
				t = cache.getById(getDB(), true, cacheInfo.getExp(), type, params);
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

	public <T> PrimaryKeyValue<T> getById(Class<T> type, Collection<PrimaryKeyParameter> primaryKeyParameters) {
		CacheConfig cacheInfo = getCacheConfig(type);
		try {
			switch (cacheInfo.getCacheType()) {
			case lazy:
				return cache.getById(getDB(), false, type, primaryKeyParameters);
			case keys:
				return cache.getById(getDB(), true, type, primaryKeyParameters);
			case full:
				return cache.getById(type, primaryKeyParameters);
			default:
				return getDB().getByIdFromDB(type, null, primaryKeyParameters);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
		getDB().iterator(tableClass, new ResultIterator() {

			public void next(Result result) {
				Object bean = result.get(tableClass);
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
			CacheConfig cacheConfig = getCacheConfig(operationBean.getBean().getClass());
			try {
				switch (cacheConfig.getCacheType()) {
				case lazy:
					transaction = cache.opHotspot(operationBean, cacheConfig.getExp(), false);
					break;
				case keys:
					transaction = cache.opHotspot(operationBean, cacheConfig.getExp(), true);
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
					cacheTransaction = new TransactionCollection(operationBeans.size());
				}
				cacheTransaction.add(transaction);
			}

			if (cacheConfig.isAsync()) {
				if (asyncList == null) {
					asyncList = new ArrayList<OperationBean>(operationBeans.size());
				}

				asyncList.add(operationBean);
			} else {
				if (synchronizationList == null) {
					synchronizationList = new ArrayList<SQL>(operationBeans.size());
				}

				synchronizationList.add(operationBean.getSql(getDB().getSqlFormat()));
			}
		}

		if (cacheAutoCommit) {
			if (cacheTransaction != null) {
				AbstractTransaction.transaction(cacheTransaction);
			}

			if (synchronizationList != null) {
				TransactionContext.getInstance().execute(getDB(), synchronizationList);
			}
		} else {
			TransactionContext.getInstance().execute(getDB(), synchronizationList, cacheTransaction);
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
		final String name = ClassUtils.getCGLIBRealClassName(tableClass);
		Logger.info("RedisHotSpotCacheStorage", "loading [" + name + "] keys to cache");
		cacheStorage.loadCache(tableClass);
		Logger.info("RedisHotSpotCacheStorage", "loading [" + name + "] keys to cache success");
	}
}
