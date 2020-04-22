package scw.db.druid;

import scw.beans.annotation.Bean;
import scw.core.Destroy;
import scw.core.instance.annotation.ResourceParameter;
import scw.core.parameter.annotation.DefaultValue;
import scw.core.utils.XUtils;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.db.DefaultDB;
import scw.db.support.DBConfigConstants;

@Bean(proxy = false)
public class DruidDB extends DefaultDB implements DBConfigConstants, Destroy {

	public DruidDB(Redis redis, @ResourceParameter @DefaultValue(DEFAULT_CONFIG) String propertiesFile) {
		super(new DruidDBConfig(propertiesFile, redis));
	}

	public DruidDB(Memcached memcached, @ResourceParameter @DefaultValue(DEFAULT_CONFIG) String propertiesFile) {
		super(new DruidDBConfig(propertiesFile, memcached));
	}

	public DruidDB(@ResourceParameter @DefaultValue(DEFAULT_CONFIG) String propertiesFile) {
		super(new DruidDBConfig(propertiesFile));
	}

	public void destroy() throws Exception {
		XUtils.destroy(getDbConfig());
	}
}
