package scw.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.utils.Assert;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.data.utils.BlockingQueue;
import scw.data.utils.DefaultBlockingQueue;
import scw.data.utils.MemcachedBlockingQueue;
import scw.data.utils.RedisBlockingQueue;
import scw.db.async.AsyncInfo;
import scw.db.async.MultipleOperation;
import scw.db.async.OperationBean;
import scw.db.cache.LazyCacheManager;
import scw.db.cache.MemcachedLazyCacheManager;
import scw.db.cache.RedisLazyCacheManager;
import scw.db.database.DataBase;
import scw.sql.Sql;
import scw.sql.orm.ORMTemplate;
import scw.sql.orm.SqlFormat;
import scw.transaction.DefaultTransactionDefinition;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.Transaction;
import scw.transaction.TransactionManager;
import scw.transaction.sql.ConnectionFactory;
import scw.transaction.sql.SqlTransactionUtils;

public abstract class DB extends ORMTemplate implements ConnectionFactory, scw.core.Destroy {
	private final LazyCacheManager cacheManager;
	private final BlockingQueue<AsyncInfo> asyncBlockingQueue;
	private boolean debug;
	private Thread syncThread;
	private boolean inIdAppendCache = true;// 使用inId查询结果是否添加到缓存

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	public boolean isDebugEnabled() {
		return debug;
	}

	public final boolean isInIdAppendCache() {
		return inIdAppendCache;
	}

	public void setInIdAppendCache(boolean inIdAppendCache) {
		this.inIdAppendCache = inIdAppendCache;
	}

	public DB() {
		this(null, new DefaultBlockingQueue<AsyncInfo>());
	};

	public DB(Memcached memcached) {
		this(memcached, null);
	}

	public DB(Redis redis) {
		this(redis, null);
	}

	public DB(Memcached memcached, String queueName) {
		Assert.notNull(memcached);
		this.cacheManager = new MemcachedLazyCacheManager(memcached);
		if (StringUtils.isEmpty(queueName)) {
			this.asyncBlockingQueue = new DefaultBlockingQueue<AsyncInfo>();
		} else {
			getLogger().trace("memcached中异步处理队列名：{}", queueName);
			this.asyncBlockingQueue = new MemcachedBlockingQueue<AsyncInfo>(memcached, queueName);
		}
		this.syncThread = new Thread(new AsyncExecute());
		this.syncThread.start();
	}

	public DB(Redis redis, String queueName) {
		Assert.notNull(redis);
		this.cacheManager = new RedisLazyCacheManager(redis);
		if (StringUtils.isEmpty(queueName)) {
			this.asyncBlockingQueue = new DefaultBlockingQueue<AsyncInfo>();
		} else {
			getLogger().trace("redis中异步处理队列名：{}", queueName);
			this.asyncBlockingQueue = new RedisBlockingQueue<AsyncInfo>(redis, queueName);
		}
		this.syncThread = new Thread(new AsyncExecute());
		this.syncThread.start();
	}

	public DB(LazyCacheManager cacheManager, BlockingQueue<AsyncInfo> asyncBlockingQueue) {
		this.cacheManager = cacheManager;
		this.asyncBlockingQueue = asyncBlockingQueue;
		this.syncThread = new Thread(new AsyncExecute());
		this.syncThread.start();
	}

	private final class AsyncExecute implements Runnable {
		public void run() {
			try {
				while (!Thread.currentThread().isInterrupted()) {
					AsyncInfo asyncInfo = asyncBlockingQueue.take();
					if (asyncInfo == null) {
						continue;
					}

					Transaction transaction = TransactionManager.getTransaction(new DefaultTransactionDefinition());
					Collection<Sql> sqls = asyncInfo.getSqls();
					MultipleOperation multipleOperation = asyncInfo.getMultipleOperation();
					try {
						if (sqls != null) {
							for (Sql sql : sqls) {
								if (sql == null) {
									continue;
								}

								execute(sql);
							}
						}

						if (multipleOperation != null) {
							List<Sql> list = multipleOperation.format(getSqlFormat());
							if (list != null) {
								for (Sql sql : list) {
									if (sql == null) {
										continue;
									}

									execute(sql);
								}
							}
						}

						TransactionManager.commit(transaction);
					} catch (Throwable e) {
						TransactionManager.rollback(transaction);
						e.printStackTrace();
					}
				}
			} catch (InterruptedException e) {
			}
		}
	}

	public void destroy() {
		syncThread.interrupt();
	}

	public abstract DataBase getDataBase();

	@Override
	public SqlFormat getSqlFormat() {
		return getDataBase().getDataBaseType().getSqlFormat();
	}

	@Override
	public Connection getUserConnection() throws SQLException {
		return SqlTransactionUtils.getTransactionConnection(this);
	}

	@Override
	public boolean save(Object bean, String tableName) {
		boolean b = super.save(bean, tableName);
		if (b && cacheManager != null) {
			cacheManager.save(bean);
		}
		return b;
	}

	@Override
	public boolean update(Object bean, String tableName) {
		boolean b = super.update(bean, tableName);
		if (b && cacheManager != null) {
			cacheManager.update(bean);
		}
		return b;
	}

	@Override
	public boolean delete(Object bean, String tableName) {
		boolean b = super.delete(bean, tableName);
		if (b && cacheManager != null) {
			cacheManager.delete(bean);
		}
		return b;
	}

	@Override
	public boolean deleteById(String tableName, Class<?> type, Object... params) {
		boolean b = super.deleteById(tableName, type, params);
		if (b && cacheManager != null) {
			cacheManager.deleteById(type, params);
		}
		return b;
	}

	@Override
	public boolean saveOrUpdate(Object bean, String tableName) {
		boolean b = super.saveOrUpdate(bean, tableName);
		if (b && cacheManager != null) {
			cacheManager.saveOrUpdate(bean);
		}
		return b;
	}

	@Override
	public <T> T getById(String tableName, Class<T> type, Object... params) {
		if (cacheManager == null) {
			return super.getById(tableName, type, params);
		}

		T t = cacheManager.getById(type, params);
		if (t == null) {
			if (cacheManager.isExist(type, params)) {
				t = super.getById(tableName, type, params);
				if (t != null) {
					cacheManager.save(t);
				}
			}
		}
		return t;
	}

	@Override
	public <K, V> Map<K, V> getInIdList(Class<V> type, String tableName, Collection<K> inIds, Object... params) {
		if (cacheManager == null) {
			return super.getInIdList(type, tableName, inIds, params);
		}

		if (inIds == null || inIds.isEmpty()) {
			return null;
		}

		Map<K, V> map = cacheManager.getInIdList(type, inIds, params);
		if (CollectionUtils.isEmpty(map)) {
			Map<K, V> valueMap = super.getInIdList(type, tableName, inIds, params);
			if (inIdAppendCache) {
				if (!CollectionUtils.isEmpty(valueMap)) {
					for (Entry<K, V> entry : valueMap.entrySet()) {
						cacheManager.save(entry.getValue());
					}
				}
			}
			return valueMap;
		}

		if (map.size() == inIds.size()) {
			return map;
		}

		List<K> notFoundList = new ArrayList<K>(inIds.size());
		for (K k : inIds) {
			if (k == null) {
				continue;
			}

			if (map.containsKey(k)) {
				continue;
			}

			notFoundList.add(k);
		}

		Map<K, V> dbMap = super.getInIdList(type, tableName, notFoundList, params);
		if (dbMap == null || dbMap.isEmpty()) {
			return map;
		}

		if (inIdAppendCache) {
			for (Entry<K, V> entry : dbMap.entrySet()) {
				cacheManager.save(entry.getValue());
			}
		}

		map.putAll(dbMap);
		return map;
	}

	public void asyncSave(Object... objs) {
		MultipleOperation multipleOperation = new MultipleOperation();
		for (Object obj : objs) {
			multipleOperation.save(obj);
		}
		asyncExecute(multipleOperation);
	}

	public void asyncUpdate(Object... objs) {
		MultipleOperation multipleOperation = new MultipleOperation();
		for (Object obj : objs) {
			multipleOperation.update(obj);
		}
		asyncExecute(multipleOperation);
	}

	public void asyncDelete(Object... objs) {
		MultipleOperation multipleOperation = new MultipleOperation();
		for (Object obj : objs) {
			multipleOperation.delete(obj);
		}
		asyncExecute(multipleOperation);
	}

	public void asyncSaveOrUpdate(Object... objs) {
		MultipleOperation multipleOperation = new MultipleOperation();
		for (Object obj : objs) {
			multipleOperation.saveOrUpdate(obj);
		}
		asyncExecute(multipleOperation);
	}

	public void asyncExecute(OperationBean... operationBeans) {
		MultipleOperation multipleOperation = new MultipleOperation();
		for (OperationBean bean : operationBeans) {
			multipleOperation.add(bean);
		}
		asyncExecute(multipleOperation);
	}

	public final void asyncExecute(MultipleOperation multipleOperation) {
		if (TransactionManager.hasTransaction()) {
			AsyncInfoTransactionLifeCycle aitlc = new AsyncInfoTransactionLifeCycle((new AsyncInfo(multipleOperation)));
			TransactionManager.transactionLifeCycle(aitlc);
		} else {
			try {
				asyncBlockingQueue.put(new AsyncInfo(multipleOperation));
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 异步执行sql语句
	 * 
	 * @param sql
	 */
	public final void asyncExecute(Sql... sql) {
		if (TransactionManager.hasTransaction()) {
			AsyncInfoTransactionLifeCycle aitlc = new AsyncInfoTransactionLifeCycle(
					(new AsyncInfo(Arrays.asList(sql))));
			TransactionManager.transactionLifeCycle(aitlc);
		} else {
			try {
				asyncBlockingQueue.put(new AsyncInfo(Arrays.asList(sql)));
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private final class AsyncInfoTransactionLifeCycle extends DefaultTransactionLifeCycle {
		private final AsyncInfo asyncInfo;

		public AsyncInfoTransactionLifeCycle(AsyncInfo asyncInfo) {
			this.asyncInfo = asyncInfo;
		}

		@Override
		public void afterProcess() {
			try {
				asyncBlockingQueue.put(asyncInfo);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			super.afterProcess();
		}
	}
}