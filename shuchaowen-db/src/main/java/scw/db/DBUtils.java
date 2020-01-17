package scw.db;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Map;

import scw.core.Constants;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.db.database.DataBase;
import scw.db.database.MysqlDataBase;
import scw.db.database.OracleDataBase;
import scw.db.database.SqlServerDataBase;
import scw.io.resource.ResourceUtils;
import scw.lang.NotSupportException;
import scw.sql.SimpleSql;
import scw.sql.Sql;
import scw.util.ConfigUtils;

public final class DBUtils {
	private static final String IGNORE_SQL_START_WITH = StringUtils
			.toString(SystemPropertyUtils.getProperty("db.file.sql.ignore.start.with"), "##");

	private DBUtils() {
	};

	@SuppressWarnings("rawtypes")
	public static void loadProperties(Object instance, Map properties) {
		ConfigUtils.loadProperties(instance, properties,
				Arrays.asList("jdbcUrl,url,host", "username,user", "password", "minSize,initialSize,minimumIdle",
						"maxSize,maxActive,maximumPoolSize", "driver,driverClass,driverClassName"));
	}

	/**
	 * 自动识别数据库 driverClassName和url两个参数至少要存在一个
	 * 
	 * @param driverClassName
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 * @throws NotSupportException
	 */
	public static DataBase automaticRecognition(String driverClassName, String url, String username, String password)
			throws NotSupportException {
		if (StringUtils.isEmpty(driverClassName) && StringUtils.isEmpty(url)) {
			throw new NotSupportException("driverClassName和url至少要存在一个有效的参数");
		}

		if (StringUtils.isEmpty(driverClassName)) {// 没有驱动名，只能根据URL来判断
			if (url.startsWith("jdbc:mysql:")) {
				return new MysqlDataBase(driverClassName, url, username, password);
			} else if (url.startsWith("jdbc:microsoft:sqlserver:")) {
				return new SqlServerDataBase(driverClassName, url, username, password);
			} else if (url.startsWith("jdbc:oracle:thin:")) {
				return new OracleDataBase(driverClassName, url, username, password);
			} else if (url.startsWith("jdbc:db2:")) {
			} else if (url.startsWith("jdbc:sybase:")) {
			} else if (url.startsWith("jdbc:informix-sqli:")) {
			} else if (url.startsWith("jdbc:postgresql:")) {
			}

		} else {// 根据驱动名称来判断
			if (driverClassName.equals("com.mysql.jdbc.Driver") || driverClassName.equals("com.mysql.cj.jdbc.Driver")) {
				return new MysqlDataBase(driverClassName, url, username, password);
			} else if (driverClassName.equals("oracle.jdbc.driver.OracleDriver")) {
				return new OracleDataBase(driverClassName, url, username, password);
			} else if (driverClassName.equals("com.microsoft.jdbc.sqlserver.SQLServerDriver")) {
				return new SqlServerDataBase(driverClassName, url, username, password);
			}
		}

		throw new NotSupportException("不支持的数据库类型,driver=" + driverClassName + ",url=" + url);
	}

	public static Collection<Sql> getSqlByFile(String path, boolean lines) {
		LinkedList<Sql> list = new LinkedList<Sql>();
		if (lines) {
			Collection<String> sqlList = ResourceUtils.getResourceOperations().getFileContentLineList(path, Constants.DEFAULT_CHARSET_NAME);
			for (String sql : sqlList) {
				if (sql.startsWith(IGNORE_SQL_START_WITH)) {
					continue;
				}

				list.add(new SimpleSql(sql));
			}
		} else {
			String sql = ResourceUtils.getResourceOperations().getFileContent(path, Constants.DEFAULT_CHARSET_NAME);
			if (!StringUtils.isEmpty(sql)) {
				list.add(new SimpleSql(sql));
			}
		}
		return list;
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
