package scw.db.hikaricp;

import java.util.Map;

import scw.core.instance.annotation.Configuration;
import scw.core.instance.annotation.ResourceParameter;
import scw.core.parameter.annotation.DefaultValue;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.db.DBUtils;
import scw.io.ResourceUtils;

@Configuration(order = Integer.MIN_VALUE)
public final class HikariCPDBConfig extends AbstractHikariCPDBConfig {
	public HikariCPDBConfig(@ResourceParameter @DefaultValue(DBUtils.DEFAULT_CONFIGURATION) String properties) {
		super(ResourceUtils.getResourceOperations().getFormattedProperties(properties).getResource());
		initByMemory(ResourceUtils.getResourceOperations().getFormattedProperties(properties).getResource());
	}

	public HikariCPDBConfig(@ResourceParameter @DefaultValue(DBUtils.DEFAULT_CONFIGURATION) String properties,
			Memcached memcached) {
		this(ResourceUtils.getResourceOperations().getFormattedProperties(properties).getResource(), memcached);
	}

	public HikariCPDBConfig(@ResourceParameter @DefaultValue(DBUtils.DEFAULT_CONFIGURATION) String properties,
			Redis redis) {
		this(ResourceUtils.getResourceOperations().getFormattedProperties(properties).getResource(), redis);
	}

	@SuppressWarnings("rawtypes")
	public HikariCPDBConfig(Map properties, Memcached memcached) {
		super(properties);
		initByMemcached(properties, memcached);
	}

	@SuppressWarnings("rawtypes")
	public HikariCPDBConfig(Map properties, Redis redis) {
		super(properties);
		initByRedis(properties, redis);
	}
}
