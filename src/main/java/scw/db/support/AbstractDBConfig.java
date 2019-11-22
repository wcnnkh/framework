package scw.db.support;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;

import scw.core.Constants;
import scw.core.Destroy;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.data.RedisDataTemplete;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.db.AsyncExecute;
import scw.db.DBConfig;
import scw.db.cache.CacheManager;
import scw.db.cache.TemporaryCacheManager;
import scw.mq.queue.MemoryQueue;
import scw.mq.queue.Queue;

@SuppressWarnings("rawtypes")
public abstract class AbstractDBConfig implements DBConfig, DBConfigConstants, Destroy {
	private String sannerTablePackage;

	public AbstractDBConfig(Map properties) {
		if (properties != null) {
			this.sannerTablePackage = StringUtils.toString(properties.get("create"),
					SystemPropertyUtils.getProperty("db.table.scanner"));
		}
	}

	public String getSannerTablePackage() {
		return sannerTablePackage;
	}

	public static CacheManager createCacheManager(Map properties, Memcached memcached) {
		String cachePrefix = StringUtils.toString(properties.get("cache.prefix"), Constants.DEFAULT_PREFIX);
		return new TemporaryCacheManager(memcached, true, cachePrefix);
	}

	public static CacheManager createCacheManager(Map properties, Redis redis) {
		String cachePrefix = StringUtils.toString(properties.get("cache.prefix"), Constants.DEFAULT_PREFIX);
		return new TemporaryCacheManager(new RedisDataTemplete(redis), true, cachePrefix);
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
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			try {
				DriverManager.deregisterDriver(drivers.nextElement());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
