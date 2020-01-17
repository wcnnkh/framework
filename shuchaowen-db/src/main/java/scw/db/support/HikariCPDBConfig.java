package scw.db.support;

import java.util.Map;

import scw.core.annotation.DefaultValue;
import scw.core.instance.annotation.ResourceParameter;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.resource.ResourceUtils;

public final class HikariCPDBConfig extends AbstractHikariCPDBConfig {
	public HikariCPDBConfig(@ResourceParameter @DefaultValue(DEFAULT_CONFIG) String properties) {
		super(ResourceUtils.getResourceOperations().getProperties(properties));
		initByMemory(ResourceUtils.getResourceOperations().getProperties(properties));
	}

	public HikariCPDBConfig(@ResourceParameter @DefaultValue(DEFAULT_CONFIG) String properties, Memcached memcached) {
		this(ResourceUtils.getResourceOperations().getProperties(properties), memcached);
	}

	public HikariCPDBConfig(@ResourceParameter @DefaultValue(DEFAULT_CONFIG) String properties, Redis redis) {
		this(ResourceUtils.getResourceOperations().getProperties(properties), redis);
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
