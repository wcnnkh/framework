package scw.db.druid;

import java.util.Map;

import scw.core.annotation.DefaultValue;
import scw.core.instance.annotation.ResourceParameter;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.io.resource.ResourceUtils;

@SuppressWarnings("rawtypes")
public final class DruidDBConfig extends AbstractDruidDBConfig {

	public DruidDBConfig(@ResourceParameter @DefaultValue(DEFAULT_CONFIG) String properties) {
		super(ResourceUtils.getResourceOperations().getProperties(properties));
		initByMemory(ResourceUtils.getResourceOperations().getProperties(properties));
	}

	public DruidDBConfig(@ResourceParameter @DefaultValue(DEFAULT_CONFIG) String properties, Memcached memcached) {
		this(ResourceUtils.getResourceOperations().getProperties(properties), memcached);
	}

	public DruidDBConfig(@ResourceParameter @DefaultValue(DEFAULT_CONFIG) String properties, Redis redis) {
		this(ResourceUtils.getResourceOperations().getProperties(properties), redis);
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
