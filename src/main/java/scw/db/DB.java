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
import scw.core.Iterator;
import scw.core.exception.NotSupportException;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.data.utils.MemcachedQueue;
import scw.data.utils.MemoryQueue;
import scw.data.utils.Queue;
import scw.data.utils.RedisQueue;
import scw.db.async.AsyncInfo;
import scw.db.async.MultipleOperation;
import scw.db.async.OperationBean;
import scw.db.cache.CacheManager;
import scw.db.cache.CacheType;
import scw.db.cache.MemcachedCacheManager;
import scw.db.cache.RedisCacheManager;
import scw.db.database.DataBase;
import scw.mq.Consumer;
import scw.mq.MQ;
import scw.mq.QueueMQ;
import scw.sql.Sql;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMTemplate;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.SqlFormat;
import scw.sql.orm.TableInfo;
import scw.sql.orm.result.Result;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;
import scw.transaction.sql.ConnectionFactory;
import scw.transaction.sql.SqlTransactionUtils;

public abstract class DB extends ORMTemplate implements ConnectionFactory, AutoCloseable {
	private static final String PREFIX = "cache:";
	private static final String KEYS_PREFIX = "keys:";
	private final CacheManager cacheManager;
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
		this.cacheManager = null;
		this.asyncService = new QueueMQ<AsyncInfo>(new MemoryQueue<AsyncInfo>());
		initAsyncService();
	};

	public DB(Memcached memcached, String queueKey) {
		this.cacheManager = new MemcachedCacheManager(memcached);
		logger.trace("memcached中异步处理队列名：{}", queueKey);
		MemcachedQueue<AsyncInfo> queue = new MemcachedQueue<AsyncInfo>(memcached, queueKey);
		QueueMQ<AsyncInfo> mq = new QueueMQ<AsyncInfo>(queue);
		mq.start();
		this.asyncService = mq;
		initAsyncService();
	}

	public DB(Redis redis, String queueKey) {
		this.cacheManager = new RedisCacheManager(redis);
		logger.trace("redis中异步处理队列名：{}", queueKey);
		Queue<AsyncInfo> queue = new RedisQueue<AsyncInfo>(redis, queueKey);
		QueueMQ<AsyncInfo> mq = new QueueMQ<AsyncInfo>(queue);
		mq.start();
		this.asyncService = mq;
		initAsyncService();
	}

	public DB(CacheManager cacheManager, MQ<AsyncInfo> asyncService) {
		this.cacheManager = cacheManager;
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

	private Map<Class<?>, CacheConfigDefinition> configCacheMap = new HashMap<Class<?>, CacheConfigDefinition>();

	/**
	 * 不处理并发,因为即使缓存无效也不会有太大的性能损失
	 * 
	 * @param tableClass
	 * @return
	 */
	protected CacheConfigDefinition getCacheConfig(Class<?> tableClass) {
		CacheConfigDefinition cacheConfigDefinition = configCacheMap.get(tableClass);
		if (cacheConfigDefinition == null) {
			cacheConfigDefinition = new CacheConfigDefinition(tableClass);
			configCacheMap.put(tableClass, cacheConfigDefinition);
		}
		return cacheConfigDefinition.isEmpty() ? null : cacheConfigDefinition;
	}

	@Override
	public void createTable(final Class<?> tableClass) {
		super.createTable(tableClass);
		final CacheConfigDefinition definition = getCacheConfig(tableClass);
		if (definition == null) {
			return;
		}

		if (definition.getCacheType() == CacheType.lazy) {
			return;
		}

		iterator(tableClass, new Iterator<Result>() {

			public void iterator(Result data) {
				Object bean = data.get(tableClass);
				try {
					loadToCache(bean, definition);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected void loadToCache(Object bean, CacheConfigDefinition config) throws Throwable {
		TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
		Object[] args = ORMUtils.getPrimaryKeys(bean, tableInfo, false);
		String objectKey = getObjectKeyById(tableInfo, args);
		if (config.getCacheType() == CacheType.keys) {
			cacheManager.add(KEYS_PREFIX + objectKey, "", config.getExp());
		} else if (config.getCacheType() == CacheType.full) {
			cacheManager.add(objectKey, bean, config.getExp());
			StringBuilder sb = new StringBuilder();
			sb.append(PREFIX).append(tableInfo.getSource().getName());
			for (int i = 0; i < args.length; i++) {
				if ((config.isFullKeys() || i > 0) && i < args.length - 1) {
					cacheManager.mapAdd(sb.toString(), args[i].toString(), objectKey);
				}

				sb.append("&");
				sb.append(args[i]);
			}
		}
	}

	private String getObjectKey(TableInfo tableInfo, Object bean)
			throws IllegalArgumentException, IllegalAccessException {
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX).append(tableInfo.getSource().getName());
		for (ColumnInfo c : tableInfo.getPrimaryKeyColumns()) {
			sb.append("&");
			sb.append(c.getField().get(bean));
		}
		return sb.toString();
	}

	private String getObjectKeyById(TableInfo tableInfo, Object... params) {
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX).append(tableInfo.getSource().getName());
		for (int i = 0; i < params.length; i++) {
			sb.append("&");
			sb.append(params[i]);
		}
		return sb.toString();
	}

	private void savefullKeys(TableInfo tableInfo, boolean full, String objectKey, Object[] primaryKeys) {
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX).append(tableInfo.getSource().getName());
		for (int i = 0; i < primaryKeys.length; i++) {
			if ((full || i > 0) && i < primaryKeys.length - 1) {
				cacheManager.mapAdd(sb.toString(), primaryKeys[i].toString(), objectKey);
			}

			sb.append("&");
			sb.append(primaryKeys[i]);
		}
	}

	private void removeFullKeys(TableInfo tableInfo, boolean full, Object[] primaryKeys) {
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX).append(tableInfo.getSource().getName());
		for (int i = 0; i < primaryKeys.length; i++) {
			if ((full || i > 0) && i < primaryKeys.length - 1) {
				cacheManager.mapRemove(sb.toString(), primaryKeys[i].toString());
			}

			sb.append("&");
			sb.append(primaryKeys[i]);
		}
	}

	public void saveToCache(final Object bean) throws Throwable {
		CacheConfigDefinition definition = getCacheConfig(bean.getClass());
		if (definition != null) {
			TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
			Object[] args = ORMUtils.getPrimaryKeys(bean, tableInfo, false);
			String objectKey = getObjectKeyById(tableInfo, args);
			cacheManager.add(objectKey, bean, definition.getExp());

			if (definition.getCacheType() == CacheType.lazy) {
				TransactionManager.transactionLifeCycle(new DeleteLazyDataTransaction(objectKey));
			} else if (definition.getCacheType() == CacheType.keys) {
				cacheManager.add(KEYS_PREFIX + objectKey, "", definition.getExp());
			} else if (definition.getCacheType() == CacheType.full) {
				savefullKeys(tableInfo, definition.isFullKeys(), objectKey, args);
			}
		}
	}

	@Override
	public boolean save(Object bean, String tableName) {
		boolean b = super.save(bean, tableName);
		if (b && cacheManager != null) {
			try {
				saveToCache(bean);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		return b;
	}

	public void updateToCache(Object bean) throws Throwable {
		CacheConfigDefinition definition = getCacheConfig(bean.getClass());
		if (definition == null) {
			return;
		}

		String objectKey = getObjectKey(ORMUtils.getTableInfo(bean.getClass()), bean);
		cacheManager.set(objectKey, bean, definition.getExp());
		if (definition.getCacheType() == CacheType.lazy) {
			TransactionManager.transactionLifeCycle(new DeleteLazyDataTransaction(objectKey));
		}
	}

	@Override
	public boolean update(Object bean, String tableName) {
		boolean b = super.update(bean, tableName);
		if (b && cacheManager != null) {
			try {
				updateToCache(bean);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		return b;
	}

	public void deleteToCache(Object bean) throws Throwable {
		CacheConfigDefinition definition = getCacheConfig(bean.getClass());
		if (definition != null) {
			TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
			Object[] args = ORMUtils.getPrimaryKeys(bean, tableInfo, false);
			String objectKey = getObjectKeyById(tableInfo, args);
			cacheManager.delete(objectKey);
			if (definition.getCacheType() == CacheType.keys) {
				cacheManager.delete(KEYS_PREFIX + objectKey);
			} else if (definition.getCacheType() == CacheType.full) {
				removeFullKeys(tableInfo, definition.isFullKeys(), args);
			}
		}
	}

	public void deleteByIdToCache(Class<?> type, Object... params) throws Throwable {
		CacheConfigDefinition definition = getCacheConfig(type);
		if (definition != null) {
			TableInfo tableInfo = ORMUtils.getTableInfo(type);
			String objectKey = getObjectKeyById(tableInfo, params);
			cacheManager.delete(objectKey);
			if (definition.getCacheType() == CacheType.keys) {
				cacheManager.delete(KEYS_PREFIX + objectKey);
			} else if (definition.getCacheType() == CacheType.full) {
				removeFullKeys(tableInfo, definition.isFullKeys(), params);
			}
		}
	}

	@Override
	public boolean delete(Object bean, String tableName) {
		boolean b = super.delete(bean, tableName);
		if (b && cacheManager != null) {
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
		if (b && cacheManager != null) {
			try {
				deleteByIdToCache(type, params);
			} catch (Throwable e) {
				new RuntimeException(e);
			}
		}
		return b;
	}

	public void saveOrUpdateToCache(Object bean) throws Throwable {
		CacheConfigDefinition definition = getCacheConfig(bean.getClass());
		if (definition != null) {
			TableInfo tableInfo = ORMUtils.getTableInfo(bean.getClass());
			Object[] args = ORMUtils.getPrimaryKeys(bean, tableInfo, false);
			String objectKey = getObjectKeyById(tableInfo, args);
			cacheManager.set(objectKey, bean, definition.getExp());
			if (definition.getCacheType() == CacheType.lazy) {
				TransactionManager.transactionLifeCycle(new DeleteLazyDataTransaction(objectKey));
			} else if (definition.getCacheType() == CacheType.keys) {
				cacheManager.add(KEYS_PREFIX + objectKey, "", definition.getExp());
			} else if (definition.getCacheType() == CacheType.full) {
				savefullKeys(tableInfo, definition.isFullKeys(), objectKey, args);
			}
		}
	}

	@Override
	public boolean saveOrUpdate(Object bean, String tableName) {
		boolean b = super.saveOrUpdate(bean, tableName);
		if (b && cacheManager != null) {
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
		if (cacheManager == null) {
			return super.getById(tableName, type, params);
		}

		CacheConfigDefinition definition = getCacheConfig(type);
		if (definition == null) {
			return super.getById(tableName, type, params);
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		String objectKey = getObjectKeyById(tableInfo, params);
		if (definition.getCacheType() == CacheType.lazy) {
			T t = cacheManager.getAndTouch(type, objectKey, definition.getExp());
			if (t == null) {
				t = super.getById(tableName, type, params);
				if (t != null) {
					cacheManager.add(objectKey, t, definition.getExp());
				}
			}
			return t;
		} else if (definition.getCacheType() == CacheType.keys) {
			T t = cacheManager.getAndTouch(type, objectKey, definition.getExp());
			if (t == null) {
				String tag = cacheManager.getAndTouch(String.class, KEYS_PREFIX + objectKey, definition.getExp());
				if (tag != null) {
					t = super.getById(tableName, type, params);
					if (t != null) {
						cacheManager.add(objectKey, t, definition.getExp());
					}
				}
			}
			return t;
		} else if (definition.getCacheType() == CacheType.full) {
			return cacheManager.get(type, objectKey);
		}

		throw new NotSupportException("不支持的缓存方式：" + definition.getCacheType());
	}

	@Override
	public <T> List<T> getByIdList(String tableName, Class<T> type, Object... params) {
		if (cacheManager == null) {
			return super.getByIdList(tableName, type, params);
		}

		CacheConfigDefinition definition = getCacheConfig(type);
		if (definition == null) {
			return super.getByIdList(tableName, type, params);
		}

		if (!definition.isFullKeys() && params.length == 0) {
			return super.getByIdList(tableName, type, params);
		}

		if (definition.getCacheType() != CacheType.full) {
			return super.getByIdList(tableName, type, params);
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		String key = getObjectKeyById(tableInfo, params);
		Map<String, String> keyMap = cacheManager.getMap(key);
		if (keyMap == null) {
			return null;
		}

		Map<String, T> valueMap = cacheManager.get(type, keyMap.values());
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
		if (cacheManager == null) {
			return super.getInIdList(type, tableName, inIds, params);
		}

		CacheConfigDefinition definition = getCacheConfig(type);
		if (definition == null) {
			return super.getInIdList(type, tableName, inIds, params);
		}

		if (definition.getCacheType() != CacheType.full) {
			return super.getInIdList(type, tableName, inIds, params);
		}

		if (params.length == 1 && !definition.isFullKeys()) {
			return super.getInIdList(type, tableName, inIds, params);
		}

		TableInfo tableInfo = ORMUtils.getTableInfo(type);
		String indexKey = getObjectKeyById(tableInfo, params);
		Map<String, String> keyMap = cacheManager.getMap(indexKey);
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

		Map<String, V> valueMap = cacheManager.get(type, map.keySet());
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
			CacheConfigDefinition definition = getCacheConfig(tableInfo.getSource());
			if (definition != null && definition.getCacheType() != CacheType.lazy) {
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

	final class DeleteLazyDataTransaction extends DefaultTransactionLifeCycle {
		private String objectKey;

		public DeleteLazyDataTransaction(String objectKey) {
			this.objectKey = objectKey;
		}

		@Override
		public void afterRollback() {
			cacheManager.delete(objectKey);
		}
	}
}