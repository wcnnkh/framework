package scw.db;

import java.util.Arrays;
import java.util.Map;

import scw.core.exception.NotSupportException;
import scw.core.utils.PropertiesUtils;
import scw.core.utils.StringUtils;
import scw.db.database.DataBase;
import scw.db.database.MysqlDataBase;
import scw.db.database.OracleDataBase;
import scw.db.database.SqlServerDataBase;

public final class DBUtils {
	private DBUtils() {
	};

	@SuppressWarnings("rawtypes")
	public static void loadProperties(Object instance, Map properties){
		PropertiesUtils.loadProperties(instance, properties,
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
}
