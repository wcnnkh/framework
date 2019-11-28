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
import scw.db.cache.TemporaryCacheManager;
import scw.mq.queue.MemoryQueue;
import scw.mq.queue.Queue;
import scw.orm.sql.SqlMapper;
import scw.orm.sql.dialect.SqlDialect;
import scw.orm.sql.support.MySqlSqlDialect;

@SuppressWarnings("rawtypes")
public abstract class AbstractDBConfig implements DBConfig, DBConfigConstants {
	private String sannerTablePackage;
	private SqlDialect sqlDialect;

	public AbstractDBConfig(Map properties) {
		if (properties != null) {
			this.sannerTablePackage = StringUtils.toString(properties.get("create"),
					SystemPropertyUtils.getProperty("db.table.scanner"));
		}

		this.sqlDialect = new MySqlSqlDialect();
	}

	public SqlDialect getSqlDialect() {
		return sqlDialect;
	}

	public final String getSannerTablePackage() {
		return sannerTablePackage;
	}

	public static CacheManager createCacheManager(Map properties, Memcached memcached,
			SqlMapper mappingOperations) {
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
}
