package scw.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.beans.annotation.Destroy;
import scw.common.Constants;
import scw.common.Iterator;
import scw.common.exception.NotSupportException;
import scw.db.annotation.CacheConfig;
import scw.db.async.AsyncInfo;
import scw.db.async.MultipleOperation;
import scw.db.async.OperationBean;
import scw.db.cache.Cache;
import scw.db.cache.CacheType;
import scw.db.cache.MemcachedCache;
import scw.db.cache.RedisCache;
import scw.db.database.DataBase;
import scw.memcached.Memcached;
import scw.mq.Consumer;
import scw.mq.MQ;
import scw.mq.QueueMQ;
import scw.redis.Redis;
import scw.sql.Sql;
import scw.sql.orm.AbstractORMTemplate;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.SqlFormat;
import scw.sql.orm.TableInfo;
import scw.sql.orm.result.Result;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;
import scw.transaction.sql.ConnectionFactory;
import scw.transaction.sql.SqlTransactionUtils;
import scw.utils.queue.MemcachedQueue;
import scw.utils.queue.MemoryQueue;
import scw.utils.queue.Queue;
import scw.utils.queue.RedisQueue;

public abstract class DB extends AbstractORMTemplate implements ConnectionFactory, AutoCloseable {
	private static final String PREFIX = "cache:";
	private static final String KEYS_PREFIX = "keys:";
	private final Cache cache;
	private final MQ<AsyncInfo> asyncService;
	private boolean debug;

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	public boolean isDebug() {
		return debug;
	}

	public DB() {
		this.cache = null;
		this.asyncService = new QueueMQ<AsyncInfo>(new MemoryQueue<AsyncInfo>());
		initAsyncService();
	};

	public DB(Memcached memcached, String queueKey) {
		this.cache = new MemcachedCache(memcached);
		logger.trace("memcached中异步处理队列名：{}", queueKey);
		MemcachedQueue<AsyncInfo> queue = new MemcachedQueue<AsyncInfo>(memcached, queueKey);
		QueueMQ<AsyncInfo> mq = new QueueMQ<AsyncInfo>(queue);
		mq.start();
		this.asyncService = mq;
		initAsyncService();
	}

	public DB(Redis redis, String queueKey) {
		this.cache = new RedisCache(redis);
		logger.trace("redis中异步处理队列名：{}", queueKey);
		Queue<AsyncInfo> queue = new RedisQueue<AsyncInfo>(redis, Constants.DEFAULT_CHARSET, queueKey);
		QueueMQ<AsyncInfo> mq = new QueueMQ<AsyncInfo>(queue);
		mq.start();
		this.asyncService = mq;
		initAsyncService();
	}

	public DB(Cache cache, MQ<AsyncInfo> asyncService) {
		this.cache = cache;
		this.asyncService = asyncService;
		initAsyncService();
	}

	private void initAsyncService() {
		if (asyncService != null) {
			asyncService.addConsumer(new Consumer<AsyncInfo>() {

				public void consumer(AsyncInfo message) {
					dbConsumer(message);
				}
			});
		}
	}

	@Destroy
	public void close() throws Exception {
		if (asyncService != null && asyncService instanceof QueueMQ) {
			((QueueMQ<AsyncInfo>) asyncService).shutdown();
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
	public void createTable(final Class<?> tableClass) {
		super.createTable(tableClass);
		final CacheConfig config = tableClass.getAnnotation(CacheConfig.class);
		if (config == null) {
			return;
		}

		if (config.type() == CacheType.lazy) {
			return;
		}

		iterator(tableClass, new Iterator<Result>() {

			public void iterator(Result data) {
				Object bean = data.get(tableClass);
				try {
					loadToCache(bean, config);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void loadToCache(Object bean, CacheConfig config) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		Object[] args = ORMUtils.getPrimaryKeys(bean, tableInfo, false);
		String objectKey = getObjectKeyById(tableInfo, args);
		if (config.type() == CacheType.keys) {
			cache.add(KEYS_PREFIX + objectKey, "", config);
		} else if (config.type() == CacheType.full) {
			cache.add(objectKey, bean, config);
			StringBuilder sb = new StringBuilder();
			sb.append(PREFIX).append(tableInfo.getClassInfo().getName());
			for (int i = 0; i < args.length; i++) {
				if ((config.fullKeys() || i > 0) && i < args.length - 1) {
					cache.mapAdd(sb.toString(), args[i].toString(), objectKey);
				}

				sb.append("&");
				sb.append(args[i]);
			}
		}
	}

	private String getObjectKey(TableInfo tableInfo, Object bean)
			throws IllegalArgumentException, IllegalAccessException {
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX).append(tableInfo.getClassInfo().getName());
		for (ColumnInfo c : tableInfo.getPrimaryKeyColumns()) {
			sb.append("&");
			sb.append(c.getFieldInfo().forceGet(bean));
		}
		return sb.toString();
	}

	private String getObjectKeyById(TableInfo tableInfo, Object... params) {
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX).append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < params.length; i++) {
			sb.append("&");
			sb.append(params[i]);
		}
		return sb.toString();
	}

	public void saveToCache(Object bean) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		CacheConfig config = tableInfo.getAnnotation(CacheConfig.class);
		if (config != null) {
			Object[] args = ORMUtils.getPrimaryKeys(bean, tableInfo, false);
			final String objectKey = getObjectKeyById(tableInfo, args);
			cache.add(objectKey, bean, config);
			if (config.type() == CacheType.lazy) {
				TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
					@Override
					public void beforeRollback() {
						cache.delete(objectKey);
						super.beforeRollback();
					}
				});
			} else if (config.type() == CacheType.keys) {
				cache.add(KEYS_PREFIX + objectKey, "", config);
			} else if (config.type() == CacheType.full) {
				StringBuilder sb = new StringBuilder();
				sb.append(PREFIX).append(tableInfo.getClassInfo().getName());
				for (int i = 0; i < args.length; i++) {
					if ((config.fullKeys() || i > 0) && i < args.length - 1) {
						cache.mapAdd(sb.toString(), args[i].toString(), objectKey);
					}

					sb.append("&");
					sb.append(args[i]);
				}
			}
		}
	}

	@Override
	public boolean save(Object bean, String tableName) {
		boolean b = super.save(bean, tableName);
		if (b && cache != null) {
			try {
				saveToCache(bean);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		return b;
	}

	public void updateToCache(Object bean) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		CacheConfig config = tableInfo.getAnnotation(CacheConfig.class);
		if (config == null) {
			return;
		}

		final String objectKey = getObjectKey(tableInfo, bean);
		cache.set(objectKey, bean, config);
		if (config.type() == CacheType.lazy) {
			TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
				@Override
				public void beforeRollback() {
					cache.delete(objectKey);
					super.beforeRollback();
				}
			});
		}
	}

	@Override
	public boolean update(Object bean, String tableName) {
		boolean b = super.update(bean, tableName);
		if (b && cache != null) {
			try {
				updateToCache(bean);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		return b;
	}

	public void deleteToCache(Object bean) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		CacheConfig config = tableInfo.getAnnotation(CacheConfig.class);
		if (config != null) {
			Object[] args = ORMUtils.getPrimaryKeys(bean, tableInfo, false);
			String objectKey = getObjectKeyById(tableInfo, args);
			cache.delete(objectKey);
			if (config.type() == CacheType.keys) {
				cache.delete(KEYS_PREFIX + objectKey);
			} else if (config.type() == CacheType.full) {
				StringBuilder sb = new StringBuilder();
				sb.append(PREFIX).append(tableInfo.getClassInfo().getName());
				for (int i = 0; i < args.length; i++) {
					if ((config.fullKeys() || i > 0) && i < args.length - 1) {
						cache.mapRemove(sb.toString(), args[i].toString());
					}

					sb.append("&");
					sb.append(args[i]);
				}
			}
		}
	}

	public void deleteByIdToCache(Class<?> type, Object... params) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		CacheConfig config = tableInfo.getAnnotation(CacheConfig.class);
		if (config != null) {
			String objectKey = getObjectKeyById(tableInfo, params);
			cache.delete(objectKey);
			if (config.type() == CacheType.keys) {
				cache.delete(KEYS_PREFIX + objectKey);
			} else if (config.type() == CacheType.full) {
				StringBuilder sb = new StringBuilder();
				sb.append(PREFIX).append(tableInfo.getClassInfo().getName());
				for (int i = 0; i < params.length; i++) {
					if ((config.fullKeys() || i > 0) && i < params.length - 1) {
						cache.mapRemove(sb.toString(), params[i].toString());
					}

					sb.append("&");
					sb.append(params[i]);
				}
			}
		}
	}

	@Override
	public boolean delete(Object bean, String tableName) {
		boolean b = super.delete(bean, tableName);
		if (b && cache != null) {
			try {
				deleteToCache(bean);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		return b;
	}

	@Override
	public boolean deleteById(String tableName, Class<?> type, Object... params) {
		boolean b = super.deleteById(tableName, type, params);
		if (b && cache != null) {
			try {
				deleteByIdToCache(type, params);
			} catch (Throwable e) {
				new RuntimeException(e);
			}
		}
		return b;
	}

	public void saveOrUpdateToCache(Object bean) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		CacheConfig config = tableInfo.getAnnotation(CacheConfig.class);
		if (config != null) {
			Object[] args = ORMUtils.getPrimaryKeys(bean, tableInfo, false);
			final String objectKey = getObjectKeyById(tableInfo, args);
			cache.set(objectKey, bean, config);
			if (config.type() == CacheType.lazy) {
				TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
					@Override
					public void beforeRollback() {
						cache.delete(objectKey);
						super.beforeRollback();
					}
				});
			} else if (config.type() == CacheType.keys) {
				cache.set(PREFIX + objectKey, "", config);
			} else if (config.type() == CacheType.full) {
				StringBuilder sb = new StringBuilder();
				sb.append(PREFIX).append(tableInfo.getClassInfo().getName());
				for (int i = 0; i < args.length; i++) {
					if ((config.fullKeys() || i > 0) && i < args.length - 1) {
						cache.mapAdd(sb.toString(), args[i].toString(), objectKey);
					}

					sb.append("&");
					sb.append(args[i]);
				}
			}
		}
	}

	@Override
	public boolean saveOrUpdate(Object bean, String tableName) {
		boolean b = super.saveOrUpdate(bean, tableName);
		if (b && cache != null) {
			try {
				saveOrUpdateToCache(bean);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		return b;
	}

	@Override
	public <T> T getById(String tableName, Class<T> type, Object... params) {
		if (cache == null) {
			return super.getById(tableName, type, params);
		}

		CacheConfig config = type.getAnnotation(CacheConfig.class);
		if (config == null) {
			return super.getById(tableName, type, params);
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		String objectKey = getObjectKeyById(tableInfo, params);
		if (config.type() == CacheType.lazy) {
			T t = cache.getAndTouch(type, objectKey, config);
			if (t == null) {
				t = super.getById(tableName, type, params);
				if (t != null) {
					cache.add(objectKey, t, config);
				}
			}
			return t;
		} else if (config.type() == CacheType.keys) {
			T t = cache.getAndTouch(type, objectKey, config);
			if (t == null) {
				String tag = cache.getAndTouch(String.class, KEYS_PREFIX + objectKey, config);
				if (tag != null) {
					t = super.getById(tableName, type, params);
					if (t != null) {
						cache.add(objectKey, t, config);
					}
				}
			}
			return t;
		} else if (config.type() == CacheType.full) {
			return cache.get(type, objectKey);
		}

		throw new NotSupportException("不支持的缓存方式：" + config.type());
	}

	@Override
	public <T> List<T> getByIdList(String tableName, Class<T> type, Object... params) {
		if (cache == null) {
			return super.getByIdList(tableName, type, params);
		}

		CacheConfig config = type.getAnnotation(CacheConfig.class);
		if (config == null) {
			return super.getByIdList(tableName, type, params);
		}

		if (config.type() != CacheType.full) {
			return super.getByIdList(tableName, type, params);
		}

		if (params.length == 1 && !config.fullKeys()) {
			return super.getByIdList(tableName, type, params);
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		String key = getObjectKeyById(tableInfo, params);
		Map<String, String> keyMap = cache.getMap(key);
		if (keyMap == null) {
			return null;
		}

		Map<String, T> valueMap = cache.get(type, keyMap.values());
		if (valueMap == null || valueMap.isEmpty()) {
			return null;
		}

		List<T> valueList = new ArrayList<T>();
		for (Entry<String, String> entry : keyMap.entrySet()) {
			valueList.add(valueMap.get(entry.getValue()));
		}
		return valueList;
	}

	@Override
	public <K, V> Map<K, V> getInIdList(Class<V> type, String tableName, Collection<K> inIds, Object... params) {
		if (cache == null) {
			return super.getInIdList(type, tableName, inIds, params);
		}

		CacheConfig config = type.getAnnotation(CacheConfig.class);
		if (config == null) {
			return super.getInIdList(type, tableName, inIds, params);
		}

		if (config.type() != CacheType.full) {
			return super.getInIdList(type, tableName, inIds, params);
		}

		if (params.length == 1 && !config.fullKeys()) {
			return super.getInIdList(type, tableName, inIds, params);
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		String indexKey = getObjectKeyById(tableInfo, params);
		Map<String, String> keyMap = cache.getMap(indexKey);
		if (keyMap == null) {
			return null;
		}

		Map<String, String> map = new HashMap<String, String>();
		for (K k : inIds) {
			if (k == null) {
				continue;
			}

			String key = k.toString();
			String objectKey = keyMap.get(key);
			map.put(objectKey, key);
		}

		if (map.isEmpty()) {
			return null;
		}

		Map<String, V> valueMap = cache.get(type, map.keySet());
		Map<K, V> result = new HashMap<K, V>(valueMap.size());
		for (K k : inIds) {
			if (k == null) {
				continue;
			}

			String key = k.toString();
			String objectKey = keyMap.get(key);
			if (objectKey == null) {
				continue;
			}

			V v = valueMap.get(objectKey);
			if (v == null) {
				continue;
			}

			result.put(k, v);
		}
		return result;
	}

	@Override
	protected boolean ormUpdateSql(TableInfo tableInfo, String tableName, Sql sql) {
		if (asyncService != null) {
			CacheConfig config = tableInfo.getAnnotation(CacheConfig.class);
			if (config != null && config.type() != CacheType.lazy) {
				if (TransactionManager.hasTransaction()) {
					AsyncInfoTransactionLifeCycle aitlc = new AsyncInfoTransactionLifeCycle(
							(new AsyncInfo(Arrays.asList(sql))));
					TransactionManager.transactionLifeCycle(aitlc);
				} else {
					asyncService.push(new AsyncInfo(Arrays.asList(sql)));
				}
				return true;
			}
		}
		return super.ormUpdateSql(tableInfo, tableName, sql);
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

	final class AsyncInfoTransactionLifeCycle extends DefaultTransactionLifeCycle {
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
