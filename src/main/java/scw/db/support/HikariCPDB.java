package scw.db.support;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

import scw.core.instance.annotation.ResourceParameter;
import scw.core.resource.ResourceUtils;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.db.DBConfig;

/**
 * 只在能java8中使用 除非你在pom引入你需要的版本
 * 
 * @author shuchaowen
 *
 */

public class HikariCPDB extends ConfigureDB implements DBConfigConstants {
	public HikariCPDB(Redis redis, @ResourceParameter(DEFAULT_CONFIG) String propertiesFile) {
		Properties properties = ResourceUtils.getProperties(propertiesFile);
		DBConfig dbConfig = new HikariCPDBConfig(properties);
		initAfter(properties, dbConfig, redis);
	}

	public HikariCPDB(Memcached memcached, @ResourceParameter(DEFAULT_CONFIG) String propertiesFile) {
		Properties properties = ResourceUtils.getProperties(propertiesFile);
		DBConfig dbConfig = new HikariCPDBConfig(properties);
		initAfter(properties, dbConfig, memcached);
	}

	public HikariCPDB(@ResourceParameter(DEFAULT_CONFIG) String propertiesFile) {
		Properties properties = ResourceUtils.getProperties(propertiesFile);
		DBConfig dbConfig = new HikariCPDBConfig(properties);
		initAfter(properties, dbConfig);
	}

	@Override
	public void destroy() {
		super.destroy();
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			try {
				DriverManager.deregisterDriver(drivers.nextElement());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
