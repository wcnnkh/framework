package scw.db;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import scw.convert.TypeDescriptor;
import scw.env.Sys;
import scw.logger.Levels;
import scw.orm.convert.EntityConversionService;
import scw.orm.convert.PropertyFactoryToEntityConversionService;
import scw.util.alias.DefaultAliasRegistry;
import scw.value.PropertyFactory;

public final class DBUtils {
	public static final String DEFAULT_CONFIGURATION = "/db/db.properties";

	private DBUtils() {
	};

	public static DefaultAliasRegistry getCommonPropertiesAliasRegistry() {
		DefaultAliasRegistry aliasRegistry = new DefaultAliasRegistry();
		aliasRegistry.registerAlias("url", "jdbcUrl");
		aliasRegistry.registerAlias("jdbcUrl", "host");

		aliasRegistry.registerAlias("username", "user");

		aliasRegistry.registerAlias("password", "pwd");

		aliasRegistry.registerAlias("minSize", "initialSize");
		aliasRegistry.registerAlias("initialSize", "minimumIdle");

		aliasRegistry.registerAlias("maxSize", "maxActive");
		aliasRegistry.registerAlias("maxActive", "maximumPoolSize");

		aliasRegistry.registerAlias("driver", "driverClass");
		aliasRegistry.registerAlias("driverClass", "driverClassName");
		return aliasRegistry;
	}

	public static void loadProperties(Object instance, PropertyFactory propertyFactory) {
		EntityConversionService configure = new PropertyFactoryToEntityConversionService();
		configure.setConversionService(Sys.env.getConversionService());
		configure.setAliasRegistry(getCommonPropertiesAliasRegistry());
		configure.setStrict(true);
		configure.setLoggerLevel(Levels.INFO.getValue());
		configure.configurationProperties(propertyFactory, TypeDescriptor.forObject(propertyFactory), instance,
				TypeDescriptor.forObject(instance));
	}

	public static void deregisterDriver() {
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		if (drivers != null) {
			while (drivers.hasMoreElements()) {
				try {
					DriverManager.deregisterDriver(drivers.nextElement());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
