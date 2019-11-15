package scw.db.support;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import scw.core.Constants;
import scw.core.Destroy;
import scw.core.utils.StringUtils;
import scw.core.utils.XUtils;
import scw.data.RedisDataTemplete;
import scw.data.TransactionContextCache;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.db.AbstractDB;
import scw.db.AsyncExecute;
import scw.db.DBConfig;
import scw.db.cache.CacheManager;
import scw.db.cache.DefaultCacheManager;
import scw.db.database.DataBase;
import scw.mq.queue.DefaultQueue;
import scw.mq.queue.Queue;

@SuppressWarnings("rawtypes")
class ConfigureDB extends AbstractDB implements Destroy {
	private CacheManager cacheManager;
	private DBConfig dbConfig;
	private Queue<AsyncExecute> asyncQueue;

	protected void initAfter(CacheManager cacheManager, DBConfig dbConfig, Queue<AsyncExecute> asyncQueue) {
		this.dbConfig = dbConfig;
		this.cacheManager = cacheManager;
		this.asyncQueue = asyncQueue;
		initAfter(Collections.EMPTY_MAP);
	}

	protected void initAfter(Map properties, DBConfig dbConfig, Memcached memcached) {
		this.dbConfig = dbConfig;
		String cachePrefix = StringUtils.toString(properties.get("cache.prefix"), Constants.DEFAULT_PREFIX);
		this.cacheManager = new DefaultCacheManager(memcached, cachePrefix);
		String queueName = StringUtils.toString(properties.get("async.name"), null);
		this.asyncQueue = StringUtils.isEmpty(queueName) ? new DefaultQueue<AsyncExecute>()
				: new DefaultQueue<AsyncExecute>(memcached, queueName);
		initAfter(properties);
	}

	protected void initAfter(Map properties, DBConfig dbConfig, Redis redis) {
		this.dbConfig = dbConfig;
		String cachePrefix = StringUtils.toString(properties.get("cache.prefix"), Constants.DEFAULT_PREFIX);
		this.cacheManager = new DefaultCacheManager(new RedisDataTemplete(redis), cachePrefix);
		String queueName = StringUtils.toString(properties.get("async.name"), null);
		this.asyncQueue = StringUtils.isEmpty(queueName) ? new DefaultQueue<AsyncExecute>()
				: new DefaultQueue<AsyncExecute>(redis, queueName);
		initAfter(properties);
	}

	protected void initAfter(Map properties, DBConfig dbConfig) {
		this.dbConfig = dbConfig;
		this.cacheManager = new DefaultCacheManager(TransactionContextCache.getInstance());
		this.asyncQueue = new DefaultQueue<AsyncExecute>();
		initAfter(properties);
	}

	private void initAfter(Map properties) {
		String createDataBase = StringUtils.toString(properties.get("create.database"), null);
		if (StringUtils.isEmpty(createDataBase)) {
			dbConfig.getDataBase().create();
		} else {
			dbConfig.getDataBase().create(createDataBase);
		}

		Object createTable = properties.get("create");
		if (createTable != null) {
			String create = createTable.toString();
			if (!StringUtils.isEmpty(create)) {
				createTable(create);
			}
		}
		asyncQueue.addConsumer(this);
	}

	public final CacheManager getCacheManager() {
		return cacheManager;
	}

	public final DBConfig getDbConfig() {
		return dbConfig;
	}

	public final Queue<AsyncExecute> getAsyncQueue() {
		return asyncQueue;
	}

	public Connection getConnection() throws SQLException {
		return dbConfig.getConnection();
	}

	public void asyncExecute(AsyncExecute asyncExecute) {
		asyncQueue.push(asyncExecute);
	}

	public DataBase getDataBase() {
		return dbConfig.getDataBase();
	}

	public void destroy() {
		XUtils.destroy(asyncQueue);
		XUtils.destroy(cacheManager);
		XUtils.destroy(dbConfig);
	}
}
