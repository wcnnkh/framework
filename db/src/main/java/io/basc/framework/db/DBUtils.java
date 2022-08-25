package io.basc.framework.db;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import io.basc.framework.logger.Levels;
import io.basc.framework.orm.support.Configurator;
import io.basc.framework.util.alias.DefaultAliasRegistry;
import io.basc.framework.value.PropertyFactory;

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
		Configurator configure = new Configurator(propertyFactory);
		configure.getContext().setAliasRegistry(getCommonPropertiesAliasRegistry());
		configure.getContext().setLoggerLevel(Levels.INFO.getValue());
		configure.transform(propertyFactory, instance);
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
