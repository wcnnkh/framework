package scw.db.hikaricp;

import scw.beans.annotation.Bean;
import scw.core.Destroy;
import scw.core.instance.annotation.ResourceParameter;
import scw.core.parameter.annotation.DefaultValue;
import scw.core.utils.XUtils;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.db.DefaultDB;
import scw.db.support.DBConfigConstants;

/**
 * 只在能java8中使用 除非你在pom引入你需要的版本
 * 
 * @author shuchaowen
 *
 */
@Bean(proxy=false)
public class HikariCPDB extends DefaultDB implements DBConfigConstants, Destroy {

	public HikariCPDB(Redis redis,
			@ResourceParameter@DefaultValue(DEFAULT_CONFIG) String propertiesFile) {
		super(new HikariCPDBConfig(propertiesFile, redis));
	}

	public HikariCPDB(Memcached memcached,
			@ResourceParameter@DefaultValue(DEFAULT_CONFIG) String propertiesFile) {
		super(new HikariCPDBConfig(propertiesFile, memcached));
	}

	public HikariCPDB(@ResourceParameter@DefaultValue(DEFAULT_CONFIG) String propertiesFile) {
		super(new HikariCPDBConfig(propertiesFile));
	}

	public void destroy() throws Exception {
		XUtils.destroy(getDbConfig());
	}
}
