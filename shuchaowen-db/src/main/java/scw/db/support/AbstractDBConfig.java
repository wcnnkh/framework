package scw.db.support;

import java.util.Map;

import scw.beans.BeanUtils;
import scw.beans.Destroy;
import scw.core.Constants;
import scw.core.GlobalPropertyFactory;
import scw.core.utils.StringUtils;
import scw.data.memcached.Memcached;
import scw.data.memory.MemoryDataManager;
import scw.data.memory.MemoryDataTemplete;
import scw.data.queue.DataMessageQueue;
import scw.data.redis.Redis;
import scw.data.redis.RedisDataTemplete;
import scw.db.AsyncExecute;
import scw.db.DBConfig;
import scw.db.MemcachedGeneratorService;
import scw.db.MemoryGeneratorService;
import scw.db.RedisGeneratorService;
import scw.sql.orm.cache.CacheManager;
import scw.sql.orm.cache.DefaultCacheManager;
import scw.sql.orm.cache.TemporaryCacheManager;
import scw.sql.orm.dialect.MySqlSqlDialect;
import scw.sql.orm.dialect.SqlDialect;
import scw.sql.orm.support.generation.GeneratorService;
import scw.util.queue.MemoryMessageQueue;
import scw.util.queue.MemoryQueue;
import scw.util.queue.MessageQueue;

@SuppressWarnings("rawtypes")
public abstract class AbstractDBConfig implements DBConfig, Destroy {
	private String sannerTablePackage;
	private SqlDialect sqlDialect;
	private CacheManager cacheManager;
	private MessageQueue<AsyncExecute> asyncQueue;
	private GeneratorService generatorService;
	private MemoryDataManager memoryDataManager;

	public AbstractDBConfig(Map properties) {
		if (properties != null) {
			this.sannerTablePackage = StringUtils.toString(properties.get("create"),
					GlobalPropertyFactory.getInstance().getString("db.table.scanner"));
		}

		this.sqlDialect = new MySqlSqlDialect();
	}

	public CacheManager getCacheManager() {
		return cacheManager;
	}

	public MessageQueue<AsyncExecute> getAsyncQueue() {
		return asyncQueue;
	}

	public GeneratorService getGeneratorService() {
		return generatorService;
	}

	public void initByMemcached(Map properties, Memcached memcached) {
		this.cacheManager = createCacheManager(properties, memcached);
		this.asyncQueue = createAsyncQueue(properties, memcached);
		this.generatorService = createMemcachedGeneratorService(properties, memcached);
	}

	public void initByRedis(Map properties, Redis redis) {
		this.cacheManager = createCacheManager(properties, redis);
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

	public static CacheManager createCacheManager(Map properties, Memcached memcached) {
		String cachePrefix = StringUtils.toString(properties.get("cache.prefix"), Constants.DEFAULT_PREFIX);
		return new TemporaryCacheManager(memcached, true, cachePrefix);
	}

	public static CacheManager createCacheManager(Map properties, Redis redis) {
		String cachePrefix = StringUtils.toString(properties.get("cache.prefix"), Constants.DEFAULT_PREFIX);
		return new TemporaryCacheManager(new RedisDataTemplete(redis), true, cachePrefix);
	}

	public static MessageQueue<AsyncExecute> createAsyncQueue(Map properties, Memcached memcached) {
		String queueName = StringUtils.toString(properties.get("async.name"), null);
		return StringUtils.isEmpty(queueName) ? new MemoryMessageQueue<AsyncExecute>()
				: new DataMessageQueue<AsyncExecute>(memcached, queueName);
	}

	public static MessageQueue<AsyncExecute> createAsyncQueue(Map properties, Redis redis) {
		String queueName = StringUtils.toString(properties.get("async.name"), null);
		return StringUtils.isEmpty(queueName) ? new MemoryMessageQueue<AsyncExecute>()
				: new DataMessageQueue<AsyncExecute>(redis, queueName);
	}

	public void destroy() throws Exception {
		BeanUtils.destroy(asyncQueue);
		BeanUtils.destroy(cacheManager);
		BeanUtils.destroy(memoryDataManager);
	}
}
