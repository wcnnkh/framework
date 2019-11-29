package scw.db.support;

import java.util.Map;

import scw.core.annotation.DefaultValue;
import scw.core.instance.annotation.ResourceParameter;
import scw.core.resource.ResourceUtils;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;

@SuppressWarnings("rawtypes")
public final class DruidDBConfig extends AbstractDruidDBConfig {

	public DruidDBConfig(@ResourceParameter @DefaultValue(DEFAULT_CONFIG) String properties) {
		super(ResourceUtils.getProperties(properties));
		initByMemory(ResourceUtils.getProperties(properties));
	}

	public DruidDBConfig(@ResourceParameter @DefaultValue(DEFAULT_CONFIG) String properties, Memcached memcached) {
		this(ResourceUtils.getProperties(properties), memcached);
	}

	public DruidDBConfig(@ResourceParameter @DefaultValue(DEFAULT_CONFIG) String properties, Redis redis) {
		this(ResourceUtils.getProperties(properties), redis);
	}

	public DruidDBConfig(Map properties, Memcached memcached) {
		super(properties);
		initByMemcached(properties, memcached);
	}

	public DruidDBConfig(Map properties, Redis redis) {
		super(properties);
		initByRedis(properties, redis);
	}
}
