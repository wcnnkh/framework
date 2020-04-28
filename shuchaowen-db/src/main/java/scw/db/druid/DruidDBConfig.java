package scw.db.druid;

import java.util.Map;

import scw.core.instance.annotation.Configuration;
import scw.core.instance.annotation.ResourceParameter;
import scw.core.parameter.annotation.DefaultValue;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.db.DBUtils;
import scw.io.ResourceUtils;

@Configuration(order=Integer.MIN_VALUE)
@SuppressWarnings("rawtypes")
public final class DruidDBConfig extends AbstractDruidDBConfig {

	public DruidDBConfig(@ResourceParameter @DefaultValue(DBUtils.DEFAULT_CONFIGURATION) String properties) {
		super(ResourceUtils.getResourceOperations().getFormattedProperties(properties));
		initByMemory(ResourceUtils.getResourceOperations().getFormattedProperties(properties));
	}

	public DruidDBConfig(@ResourceParameter @DefaultValue(DBUtils.DEFAULT_CONFIGURATION) String properties, Memcached memcached) {
		this(ResourceUtils.getResourceOperations().getFormattedProperties(properties), memcached);
	}

	public DruidDBConfig(@ResourceParameter @DefaultValue(DBUtils.DEFAULT_CONFIGURATION) String properties, Redis redis) {
		this(ResourceUtils.getResourceOperations().getFormattedProperties(properties), redis);
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
