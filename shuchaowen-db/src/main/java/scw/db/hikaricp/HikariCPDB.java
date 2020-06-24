package scw.db.hikaricp;

import scw.beans.BeanUtils;
import scw.beans.Destroy;
import scw.core.instance.annotation.ResourceParameter;
import scw.core.parameter.annotation.DefaultValue;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.db.DBUtils;
import scw.db.DefaultDB;

/**
 * 只在能java8中使用 除非你在pom引入你需要的版本
 * 
 * @author shuchaowen
 *
 */
public class HikariCPDB extends DefaultDB implements Destroy {

	public HikariCPDB(Redis redis,
			@ResourceParameter@DefaultValue(DBUtils.DEFAULT_CONFIGURATION) String propertiesFile) {
		super(new HikariCPDBConfig(propertiesFile, redis));
	}

	public HikariCPDB(Memcached memcached,
			@ResourceParameter@DefaultValue(DBUtils.DEFAULT_CONFIGURATION) String propertiesFile) {
		super(new HikariCPDBConfig(propertiesFile, memcached));
	}

	public HikariCPDB(@ResourceParameter@DefaultValue(DBUtils.DEFAULT_CONFIGURATION) String propertiesFile) {
		super(new HikariCPDBConfig(propertiesFile));
	}

	public void destroy() throws Exception {
		BeanUtils.destroy(getDbConfig());
	}
}
