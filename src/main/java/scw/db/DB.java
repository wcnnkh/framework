package scw.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import scw.core.Consumer;
import scw.core.exception.NotSupportException;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.utils.Assert;
import scw.core.utils.StringUtils;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.data.utils.MemcachedQueue;
import scw.data.utils.MemoryQueue;
import scw.data.utils.Queue;
import scw.data.utils.RedisQueue;
import scw.db.async.AsyncInfo;
import scw.db.async.MultipleOperation;
import scw.db.async.OperationBean;
import scw.db.cache.LazyCacheManager;
import scw.db.cache.MemcachedLazyCacheManager;
import scw.db.cache.RedisLazyCacheManager;
import scw.db.database.DataBase;
import scw.mq.MQ;
import scw.mq.QueueMQ;
import scw.sql.Sql;
import scw.sql.orm.ORMTemplate;
import scw.sql.orm.SqlFormat;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;
import scw.transaction.sql.ConnectionFactory;
import scw.transaction.sql.SqlTransactionUtils;

public abstract class DB extends ORMTemplate implements ConnectionFactory, scw.core.Destroy {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private final LazyCacheManager cacheManager;
	private final MQ<AsyncInfo> asyncService;
	private final boolean destroyAsyncService;
	private boolean debug;

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	public boolean isDebugEnabled() {
		return debug;
	}

	public DB() {
		this.cacheManager = null;
		QueueMQ<AsyncInfo> mq = new QueueMQ<AsyncInfo>(new MemoryQueue<AsyncInfo>());
		mq.start();
		this.asyncService = mq;
		this.destroyAsyncService = true;
		initAsyncService();
	};

	public DB(Memcached memcached) {
		this(memcached, null);
	}

	public DB(Redis redis) {
		this(redis, null);
	}

	public DB(Memcached memcached, String queueKey) {
		Assert.notNull(memcached);
		this.cacheManager = new MemcachedLazyCacheManager(memcached);
		QueueMQ<AsyncInfo> mq;
		if (StringUtils.isEmpty(queueKey)) {
			mq = new QueueMQ<AsyncInfo>(new MemoryQueue<AsyncInfo>());
		} else {
			getLogger().trace("memcached中异步处理队列名：{}", queueKey);
			MemcachedQueue<AsyncInfo> queue = new MemcachedQueue<AsyncInfo>(memcached, queueKey);
			mq = new QueueMQ<AsyncInfo>(queue);
		}
		mq.start();
		this.asyncService = mq;
		this.destroyAsyncService = true;
		initAsyncService();
	}

	public DB(Redis redis, String queueKey) {
		Assert.notNull(redis);
		this.cacheManager = new RedisLazyCacheManager(redis);
		QueueMQ<AsyncInfo> mq;
		if (StringUtils.isEmpty(queueKey)) {
			mq = new QueueMQ<AsyncInfo>(new MemoryQueue<AsyncInfo>());
		} else {
			getLogger().trace("redis中异步处理队列名：{}", queueKey);
			Queue<AsyncInfo> queue = new RedisQueue<AsyncInfo>(redis, queueKey);
			mq = new QueueMQ<AsyncInfo>(queue);
		}
		mq.start();
		this.asyncService = mq;
		this.destroyAsyncService = true;
		initAsyncService();
	}

	public DB(LazyCacheManager cacheManager, MQ<AsyncInfo> asyncService) {
		this.cacheManager = cacheManager;
		this.asyncService = asyncService;
		this.destroyAsyncService = false;
		initAsyncService();
	}

	private void initAsyncService() {
		if (asyncService != null) {
			asyncService.addConsumer(new Consumer<AsyncInfo>() {

				public void consume(AsyncInfo message) {
					dbConsumer(message);
				}
			});
		}
	}

	public void destroy() {
		if (destroyAsyncService) {
			if (asyncService != null && asyncService instanceof QueueMQ) {
				((QueueMQ<AsyncInfo>) asyncService).destroy();
			}
		}
	}

	private void dbConsumer(AsyncInfo asyncInfo) {
		try {
			asyncInfo.execute(this, getSqlFormat());
		} catch (Throwable e) {
			e.printStackTrace();
		}
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
		if (map == null || map.isEmpty()) {
			return super.getInIdList(type, tableName, inIds, params);
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

	public void asyncExecute(MultipleOperation multipleOperation) {
		if (asyncService == null) {
			throw new NotSupportException("不支持异步执行sql语句");
		}

		if (TransactionManager.hasTransaction()) {
			AsyncInfoTransactionLifeCycle aitlc = new AsyncInfoTransactionLifeCycle((new AsyncInfo(multipleOperation)));
			TransactionManager.transactionLifeCycle(aitlc);
		} else {
			asyncService.push(new AsyncInfo(multipleOperation));
		}
	}

	/**
	 * 异步执行sql语句
	 * 
	 * @param sql
	 */
	public void asyncExecute(Sql... sql) {
		if (asyncService == null) {
			throw new NotSupportException("不支持异步执行sql语句");
		}

		if (TransactionManager.hasTransaction()) {
			AsyncInfoTransactionLifeCycle aitlc = new AsyncInfoTransactionLifeCycle(
					(new AsyncInfo(Arrays.asList(sql))));
			TransactionManager.transactionLifeCycle(aitlc);
		} else {
			asyncService.push(new AsyncInfo(Arrays.asList(sql)));
		}
	}

	private final class AsyncInfoTransactionLifeCycle extends DefaultTransactionLifeCycle {
		private final AsyncInfo asyncInfo;

		public AsyncInfoTransactionLifeCycle(AsyncInfo asyncInfo) {
			this.asyncInfo = asyncInfo;
		}

		@Override
		public void afterProcess() {
			asyncService.push(asyncInfo);
			super.afterProcess();
		}
	}
}