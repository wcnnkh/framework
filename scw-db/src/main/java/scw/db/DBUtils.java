package scw.db;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import scw.configure.support.ConfigureUtils;
import scw.configure.support.EntityConfigure;
import scw.configure.support.PropertyFactoryConfigure;
import scw.convert.TypeDescriptor;
import scw.core.utils.StringUtils;
import scw.db.database.DataBase;
import scw.db.database.MysqlDataBase;
import scw.db.database.OracleDataBase;
import scw.db.database.SqlServerDataBase;
import scw.lang.NotSupportedException;
import scw.util.alias.DefaultAliasRegistry;
import scw.value.factory.PropertyFactory;

public final class DBUtils {
	public static final String DEFAULT_CONFIGURATION = "/db/db.properties";

	private DBUtils() {
	};
	
	public static DefaultAliasRegistry getCommonPropertiesAliasRegistry(){
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
		EntityConfigure configure = new PropertyFactoryConfigure(ConfigureUtils.getConversionServiceFactory());
		configure.setAliasRegistry(getCommonPropertiesAliasRegistry());
		configure.setStrict(true);
		configure.configuration(propertyFactory, TypeDescriptor.forObject(propertyFactory), instance, TypeDescriptor.forObject(instance));
	}
	
	/**
	 * 自动识别数据库 driverClassName和url两个参数至少要存在一个
	 * 
	 * @param driverClassName
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 * @throws NotSupportedException
	 */
	public static DataBase automaticRecognition(String driverClassName,
			String url, String username, String password)
			throws NotSupportedException {
		if (StringUtils.isEmpty(driverClassName) && StringUtils.isEmpty(url)) {
			throw new NotSupportedException("driverClassName和url至少要存在一个有效的参数");
		}

		if (StringUtils.isEmpty(driverClassName)) {// 没有驱动名，只能根据URL来判断
			if (url.startsWith("jdbc:mysql:")) {
				return new MysqlDataBase(driverClassName, url, username,
						password);
			} else if (url.startsWith("jdbc:microsoft:sqlserver:")) {
				return new SqlServerDataBase(driverClassName, url, username,
						password);
			} else if (url.startsWith("jdbc:oracle:thin:")) {
				return new OracleDataBase(driverClassName, url, username,
						password);
			} else if (url.startsWith("jdbc:db2:")) {
			} else if (url.startsWith("jdbc:sybase:")) {
			} else if (url.startsWith("jdbc:informix-sqli:")) {
			} else if (url.startsWith("jdbc:postgresql:")) {
			}

		} else {// 根据驱动名称来判断
			if (driverClassName.equals("com.mysql.jdbc.Driver")
					|| driverClassName.equals("com.mysql.cj.jdbc.Driver")) {
				return new MysqlDataBase(driverClassName, url, username,
						password);
			} else if (driverClassName
					.equals("oracle.jdbc.driver.OracleDriver")) {
				return new OracleDataBase(driverClassName, url, username,
						password);
			} else if (driverClassName
					.equals("com.microsoft.jdbc.sqlserver.SQLServerDriver")) {
				return new SqlServerDataBase(driverClassName, url, username,
						password);
			}
		}

		throw new NotSupportedException("不支持的数据库类型,driver=" + driverClassName
				+ ",url=" + url);
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
