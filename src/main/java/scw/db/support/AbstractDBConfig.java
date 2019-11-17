package scw.db.support;

import java.util.Map;

import scw.core.Constants;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.data.RedisDataTemplete;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.db.AsyncExecute;
import scw.db.DBConfig;
import scw.db.cache.CacheManager;
import scw.db.cache.DefaultCacheManager;
import scw.mq.queue.MemoryQueue;
import scw.mq.queue.Queue;

@SuppressWarnings("rawtypes")
public abstract class AbstractDBConfig implements DBConfig, DBConfigConstants{
	private String sannerTablePackage;

	public AbstractDBConfig(Map properties) {
		if (properties != null) {
			this.sannerTablePackage = StringUtils.toString(
					properties.get("create"),
					SystemPropertyUtils.getProperty("db.table.scanner"));
		}
	}

	public String getSannerTablePackage() {
		return sannerTablePackage;
	}

	public static CacheManager createCacheManager(Map properties,
			Memcached memcached) {
		String cachePrefix = StringUtils.toString(
				properties.get("cache.prefix"), Constants.DEFAULT_PREFIX);
		return new DefaultCacheManager(memcached, cachePrefix);
	}

	public static CacheManager createCacheManager(Map properties, Redis redis) {
		String cachePrefix = StringUtils.toString(
				properties.get("cache.prefix"), Constants.DEFAULT_PREFIX);
		return new DefaultCacheManager(new RedisDataTemplete(redis),
				cachePrefix);
	}

	public static Queue<AsyncExecute> createAsyncQueue(Map properties,
			Memcached memcached) {
		String queueName = StringUtils.toString(properties.get("async.name"),
				null);
		return StringUtils.isEmpty(queueName) ? new MemoryQueue<AsyncExecute>()
				: new MemoryQueue<AsyncExecute>(memcached, queueName);
	}

	public static Queue<AsyncExecute> createAsyncQueue(Map properties,
			Redis redis) {
		String queueName = StringUtils.toString(properties.get("async.name"),
				null);
		return StringUtils.isEmpty(queueName) ? new MemoryQueue<AsyncExecute>()
				: new MemoryQueue<AsyncExecute>(redis, queueName);
	}
}
