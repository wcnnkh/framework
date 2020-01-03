package scw.db.support;

import java.util.Map;

import scw.core.Constants;
import scw.core.Destroy;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.core.utils.XUtils;
import scw.data.RedisDataTemplete;
import scw.data.memcached.Memcached;
import scw.data.memory.MemoryDataManager;
import scw.data.memory.MemoryDataTemplete;
import scw.data.redis.Redis;
import scw.db.AsyncExecute;
import scw.db.DBConfig;
import scw.db.cache.CacheManager;
import scw.db.cache.DefaultCacheManager;
import scw.db.cache.TemporaryCacheManager;
import scw.mq.queue.MemoryQueue;
import scw.mq.queue.Queue;
import scw.orm.sql.GeneratorService;
import scw.orm.sql.SqlMapper;
import scw.orm.sql.SqlORMUtils;
import scw.orm.sql.dialect.SqlDialect;
import scw.orm.sql.support.MemcachedGeneratorService;
import scw.orm.sql.support.MemoryGeneratorService;
import scw.orm.sql.support.MySqlSqlDialect;
import scw.orm.sql.support.RedisGeneratorService;

@SuppressWarnings("rawtypes")
public abstract class AbstractDBConfig implements DBConfig, DBConfigConstants, Destroy {
	private String sannerTablePackage;
	private SqlDialect sqlDialect;
	private CacheManager cacheManager;
	private Queue<AsyncExecute> asyncQueue;
	private GeneratorService generatorService;
	private MemoryDataManager memoryDataManager;

	public AbstractDBConfig(Map properties) {
		if (properties != null) {
			this.sannerTablePackage = StringUtils.toString(properties.get("create"),
					SystemPropertyUtils.getProperty("db.table.scanner"));
		}

		this.sqlDialect = new MySqlSqlDialect();
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public Queue<AsyncExecute> getAsyncQueue() {
		return asyncQueue;
	}

	public GeneratorService getGeneratorService() {
		return generatorService;
	}

	public void initByMemcached(Map properties, Memcached memcached) {
		this.cacheManager = createCacheManager(properties, memcached, SqlORMUtils.getSqlMapper());
		this.asyncQueue = createAsyncQueue(properties, memcached);
		this.generatorService = createMemcachedGeneratorService(properties, memcached);
	}

	public void initByRedis(Map properties, Redis redis) {
		this.cacheManager = createCacheManager(properties, redis, SqlORMUtils.getSqlMapper());
		this.asyncQueue = createAsyncQueue(properties, redis);
		this.generatorService = createRedisGeneratorService(properties, redis);
	}

	public void initByMemory(Map properties) {
		this.cacheManager = new DefaultCacheManager();
		this.asyncQueue = new MemoryQueue<AsyncExecute>();
		this.memoryDataManager = new MemoryDataManager();
		this.generatorService = new MemoryGeneratorService(new MemoryDataTemplete(memoryDataManager));
	}

	public SqlDialect getSqlDialect() {
		return sqlDialect;
	}

	public final String getSannerTablePackage() {
		return sannerTablePackage;
	}

	public static GeneratorService createMemcachedGeneratorService(Map properties, Memcached memcached) {
		return new MemcachedGeneratorService(memcached);
	}

	public static GeneratorService createRedisGeneratorService(Map properties, Redis redis) {
		return new RedisGeneratorService(redis);
	}

	public static CacheManager createCacheManager(Map properties, Memcached memcached, SqlMapper mappingOperations) {
		String cachePrefix = StringUtils.toString(properties.get("cache.prefix"), Constants.DEFAULT_PREFIX);
		return new TemporaryCacheManager(mappingOperations, memcached, true, cachePrefix);
	}

	public static CacheManager createCacheManager(Map properties, Redis redis, SqlMapper mappingOperations) {
		String cachePrefix = StringUtils.toString(properties.get("cache.prefix"), Constants.DEFAULT_PREFIX);
		return new TemporaryCacheManager(mappingOperations, new RedisDataTemplete(redis), true, cachePrefix);
	}

	public static Queue<AsyncExecute> createAsyncQueue(Map properties, Memcached memcached) {
		String queueName = StringUtils.toString(properties.get("async.name"), null);
		return StringUtils.isEmpty(queueName) ? new MemoryQueue<AsyncExecute>()
				: new MemoryQueue<AsyncExecute>(memcached, queueName);
	}

	public static Queue<AsyncExecute> createAsyncQueue(Map properties, Redis redis) {
		String queueName = StringUtils.toString(properties.get("async.name"), null);
		return StringUtils.isEmpty(queueName) ? new MemoryQueue<AsyncExecute>()
				: new MemoryQueue<AsyncExecute>(redis, queueName);
	}

	public void destroy() {
		XUtils.destroy(asyncQueue);
		XUtils.destroy(cacheManager);
		XUtils.destroy(memoryDataManager);
	}
}
