package scw.db.druid;

import scw.beans.BeanUtils;
import scw.beans.Destroy;
import scw.core.instance.annotation.ResourceParameter;
import scw.core.parameter.annotation.DefaultValue;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.db.DBUtils;
import scw.db.DefaultDB;

public class DruidDB extends DefaultDB implements Destroy {

	public DruidDB(Redis redis, @ResourceParameter @DefaultValue(DBUtils.DEFAULT_CONFIGURATION) String propertiesFile) {
		super(new DruidDBConfig(propertiesFile, redis));
	}

	public DruidDB(Memcached memcached, @ResourceParameter @DefaultValue(DBUtils.DEFAULT_CONFIGURATION) String propertiesFile) {
		super(new DruidDBConfig(propertiesFile, memcached));
	}

	public DruidDB(@ResourceParameter @DefaultValue(DBUtils.DEFAULT_CONFIGURATION) String propertiesFile) {
		super(new DruidDBConfig(propertiesFile));
	}

	public void destroy() throws Exception {
		BeanUtils.destroy(getDbConfig());
	}
}
