package scw.db.support;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import scw.core.Constants;
import scw.core.instance.InstanceUtils;
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
import scw.orm.Filter;
import scw.orm.sql.DefaultSqlMappingOperations;
import scw.orm.sql.SqlMappingOperations;
import scw.orm.sql.dialect.SqlDialect;
import scw.orm.sql.dialect.mysql.MySqlSqlDialect;
import scw.orm.sql.support.DefaultSqlFilter;

@SuppressWarnings("rawtypes")
public abstract class AbstractDBConfig implements DBConfig, DBConfigConstants {
	private String sannerTablePackage;
	private SqlMappingOperations sqlMappingOperations;
	private SqlDialect sqlDialect;

	public AbstractDBConfig(Map properties) {
		if (properties != null) {
			this.sannerTablePackage = StringUtils.toString(properties.get("create"),
					SystemPropertyUtils.getProperty("db.table.scanner"));
		}

		this.sqlMappingOperations = createSqlMappingOperations(createFitlers(properties));
		this.sqlDialect = new MySqlSqlDialect();
	}

	protected Collection<Filter> createFitlers(Map properties) {
		List<Filter> list = new LinkedList<Filter>();
		String filterNames = StringUtils.toString(properties.get("filters"), null);
		if (StringUtils.isEmpty(filterNames)) {
			for (String name : StringUtils.commonSplit(filterNames)) {
				list.add((Filter) InstanceUtils.getInstance(name));
			}
		}
		list.add(new DefaultSqlFilter());
		return list;
	}

	public SqlDialect getSqlDialect() {
		return sqlDialect;
	}

	protected SqlMappingOperations createSqlMappingOperations(Collection<Filter> filters) {
		return new DefaultSqlMappingOperations(filters, filters);
	}

	public final String getSannerTablePackage() {
		return sannerTablePackage;
	}

	public final SqlMappingOperations getSqlMappingOperations() {
		return sqlMappingOperations;
	}

	public static CacheManager createCacheManager(Map properties, Memcached memcached,
			SqlMappingOperations mappingOperations) {
		String cachePrefix = StringUtils.toString(properties.get("cache.prefix"), Constants.DEFAULT_PREFIX);
		return new TemporaryCacheManager(mappingOperations, memcached, true, cachePrefix);
	}

	public static CacheManager createCacheManager(Map properties, Redis redis, SqlMappingOperations mappingOperations) {
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
