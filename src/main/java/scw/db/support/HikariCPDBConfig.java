package scw.db.support;

import java.util.Map;

import scw.core.annotation.DefaultValue;
import scw.core.instance.annotation.ResourceParameter;
import scw.core.resource.ResourceUtils;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;

public final class HikariCPDBConfig extends AbstractHikariCPDBConfig {
	public HikariCPDBConfig(@ResourceParameter @DefaultValue(DEFAULT_CONFIG) String properties) {
		super(ResourceUtils.getProperties(properties));
		initByMemory(ResourceUtils.getProperties(properties));
	}

	public HikariCPDBConfig(@ResourceParameter @DefaultValue(DEFAULT_CONFIG) String properties, Memcached memcached) {
		this(ResourceUtils.getProperties(properties), memcached);
	}

	public HikariCPDBConfig(@ResourceParameter @DefaultValue(DEFAULT_CONFIG) String properties, Redis redis) {
		this(ResourceUtils.getProperties(properties), redis);
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
