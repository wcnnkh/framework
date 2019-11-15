package scw.db.support;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;

import scw.beans.annotation.Bean;
import scw.core.instance.annotation.ResourceParameter;
import scw.core.resource.ResourceUtils;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.db.DBConfig;

@Bean(proxy = false)
public class DruidDB extends ConfigureDB implements DBConfigConstants {

	public DruidDB(Redis redis, @ResourceParameter(DEFAULT_CONFIG) String propertiesFile) {
		Properties properties = ResourceUtils.getProperties(propertiesFile);
		DBConfig dbConfig = new DruidDBConfig(properties);
		initAfter(properties, dbConfig, redis);
	}

	public DruidDB(Memcached memcached, @ResourceParameter(DEFAULT_CONFIG) String propertiesFile) {
		Properties properties = ResourceUtils.getProperties(propertiesFile);
		DBConfig dbConfig = new DruidDBConfig(properties);
		initAfter(properties, dbConfig, memcached);
	}

	public DruidDB(@ResourceParameter(DEFAULT_CONFIG) String propertiesFile) {
		Properties properties = ResourceUtils.getProperties(propertiesFile);
		DBConfig dbConfig = new DruidDBConfig(properties);
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
