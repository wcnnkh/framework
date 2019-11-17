package scw.db.support;

import scw.beans.annotation.Bean;
import scw.core.Destroy;
import scw.core.instance.annotation.ResourceParameter;
import scw.core.utils.XUtils;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.db.DefaultDB;

@Bean(proxy = false)
public class DruidDB extends DefaultDB implements DBConfigConstants, Destroy {

	public DruidDB(Redis redis,
			@ResourceParameter(DEFAULT_CONFIG) String propertiesFile) {
		super(new DruidDBConfig(propertiesFile, redis));
	}

	public DruidDB(Memcached memcached,
			@ResourceParameter(DEFAULT_CONFIG) String propertiesFile) {
		super(new DruidDBConfig(propertiesFile, memcached));
	}

	public DruidDB(@ResourceParameter(DEFAULT_CONFIG) String propertiesFile) {
		super(new DruidDBConfig(propertiesFile));
	}

	public void destroy() {
		XUtils.destroy(getDbConfig());
	}
}
