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
import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.transaction.AbstractTransaction;
import scw.common.transaction.Transaction;
import scw.common.transaction.TransactionCollection;
import scw.common.utils.ClassUtils;
import scw.database.DataBaseUtils;
import scw.database.Result;
import scw.database.TableInfo;
import scw.database.TransactionContext;
import scw.database.annoation.Table;
import scw.db.AbstractDB;
import scw.db.DBUtils;
import scw.db.OperationBean;
import scw.db.storage.Storage;
import scw.sql.Sql;
import scw.utils.memcached.Memcached;
import scw.utils.queue.MemcachedQueue;
import scw.utils.queue.Queue;
import scw.utils.queue.RedisQueue;
import scw.utils.redis.Redis;

public final class CacheStorage implements Storage {
	private final Map<String, CacheConfig> cacheConfigMap = new HashMap<String, CacheConfig>();
	private final Cache cache;
	private final AbstractDB db;
	// 缓存是否自动提交，如果为false就是要等transaction.commit()的时候再提交
	// 默认参与事务，应该以保证数据完整性为主
	private boolean cacheAutoCommit = false;
	private final Queue<Collection<OperationBean>> queue;
	private CacheConfig defaultCacheConfig;
	private Thread thread;

	public CacheStorage(AbstractDB db, Memcached memcached, String queueKey) {
		this.db = db;
		this.cache = new MemcachedCache(memcached);
		this.queue = new MemcachedQueue<Collection<OperationBean>>(memcached, queueKey);
	}

	public void setDefaultCacheConfig(CacheConfig defaultCacheConfig) {
		this.defaultCacheConfig = defaultCacheConfig;
	}

	public CacheStorage(AbstractDB db, Redis redis, String queueKey) {
		this(db, redis, queueKey, Charset.forName("UTF-8"));
	}

	public CacheStorage(AbstractDB db, Redis redis, String queueKey, Charset charset) {
		this.db = db;
		this.cache = new RedisCache(redis, charset);
		this.queue = new RedisQueue<Collection<OperationBean>>(redis, charset, queueKey);
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

	public void config(CacheType cacheType, int exp, boolean isAsync, Class<?>... tableClass) {
		config(new CacheConfig(cacheType, exp, isAsync), tableClass);
	}

	public void config(CacheType cacheType, boolean isAsync, Class<?>... tableClass) {
		config(new CacheConfig(cacheType, CacheConfig.DATA_DEFAULT_EXP_TIME, isAsync), tableClass);
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

	private boolean dbExist(OperationBean operationBean) throws IllegalArgumentException, IllegalAccessException {
		TableInfo tableInfo = DataBaseUtils.getTableInfo(operationBean.getBean().getClass());
		Object[] params = new Object[tableInfo.getPrimaryKeyColumns().length];
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			params[i] = tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(operationBean.getBean());
		}

		return db.getById(tableInfo.getClassInfo().getClz(), params) != null;
	}

	public void init() {
		thread = new Thread(new Runnable() {

			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					try {
						Collection<OperationBean> beans = queue.take();
						TransactionContext.begin();
						try {
							Collection<Sql> sqls = DBUtils.getSqlList(db.getSqlFormat(), beans);
							if (sqls == null || sqls.isEmpty()) {
								return;
							}

							TransactionCollection collection = new TransactionCollection();
							for (OperationBean operationBean : beans) {
								CacheConfig cacheConfig = getCacheConfig(operationBean.getBean().getClass());
								switch (cacheConfig.getCacheType()) {
								case keys:
								case lazy:
									boolean exist = dbExist(operationBean);
									Transaction transaction = new HostspotDataAsyncRollbackTransaction(exist,
											cacheConfig.getCacheType() == CacheType.keys, cache, operationBean);
									collection.add(transaction);
									break;
								default:
									break;
								}
							}

							TransactionContext.execute(db, sqls);
							TransactionContext.execute(collection);
							TransactionContext.commit();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							TransactionContext.end();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}, this.getClass().getName());
		thread.start();

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

	public void destroy() {
		thread.interrupt();
	}

	protected CacheConfig getCacheConfig(Class<?> tableClass) {
		CacheConfig cacheInfo = cacheConfigMap.get(ClassUtils.getProxyRealClassName(tableClass));
		if (cacheInfo == null) {
			return defaultCacheConfig == null ? CacheConfig.DEFAULT_CACHE_CONFIG : defaultCacheConfig;
		}
		return cacheInfo;
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
				t = getDB().getById(type, null, params);
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
			return getDB().getByIdList(type, params);
		}
	}

	protected void loadCache(final Class<?> tableClass) {
		final CacheConfig cacheInfo = getCacheConfig(tableClass);
		getDB().iterator(tableClass, new Iterator<Result>() {

			public void iterator(Result data) {
				Object bean = data.get(tableClass);
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
		List<Sql> synchronizationList = null;// 同步列表
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
					cacheTransaction = new TransactionCollection();
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
					synchronizationList = new ArrayList<Sql>(operationBeans.size());
				}

				synchronizationList.add(operationBean.getSql(getDB().getSqlFormat()));
			}
		}

		if (cacheAutoCommit) {
			if (cacheTransaction != null) {
				AbstractTransaction.transaction(cacheTransaction);
			}

			if (synchronizationList != null) {
				TransactionContext.execute(getDB(), synchronizationList);
			}
		} else {
			TransactionContext.execute(getDB(), synchronizationList);
			TransactionContext.execute(cacheTransaction);
		}

		if (asyncList != null) {
			if (!queue.offer(asyncList)) {
				throw new ShuChaoWenRuntimeException("add queue error");
			}
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
		Logger.info("RedisHotSpotCacheStorage", "loading [" + name + "] keys to cache");
		cacheStorage.loadCache(tableClass);
		Logger.info("RedisHotSpotCacheStorage", "loading [" + name + "] keys to cache success");
	}
}
