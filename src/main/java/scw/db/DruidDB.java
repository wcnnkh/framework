package scw.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import scw.common.utils.ConfigUtils;
import scw.common.utils.PropertiesUtils;
import scw.common.utils.StringUtils;
import scw.db.sql.MysqlFormat;
import scw.db.sql.SQLFormat;

import com.alibaba.druid.pool.DruidDataSource;

public final class DruidDB extends DB {
	private DruidDataSource datasource;

	/**
	 * 数据库配置文件目录 只支持mysql
	 * 
	 * @param propertiesFilePath
	 */
	public DruidDB(String propertiesFilePath) {
		this(new MysqlFormat(), propertiesFilePath, "utf-8");
	}

	public DruidDB(SQLFormat sqlFormat, String propertiesFilePath,
			String charsetName) {
		this(sqlFormat, ConfigUtils.getProperties(propertiesFilePath,
				charsetName));
	}

	public DruidDB(SQLFormat sqlFormat, Properties properties) {
		super(sqlFormat);
		String url = PropertiesUtils.getProperty(properties, "jdbcUrl", "url",
				"host");
		String username = PropertiesUtils.getProperty(properties, "username",
				"user", "name");
		String password = PropertiesUtils.getProperty(properties, "password",
				"pwd");
		String minSize = PropertiesUtils.getProperty(properties, "minSize",
				"initialSize", "min");
		String maxSize = PropertiesUtils.getProperty(properties, "maxSize",
				"maxActive", "max");
		String driver = PropertiesUtils.getProperty(properties, "driver",
				"driverClass", "driverClassName");
		String maxPoolPreparedStatementPerConnectionSize = PropertiesUtils
				.getProperty(properties,
						"maxPoolPreparedStatementPerConnectionSize");

		datasource = new DruidDataSource();
		datasource.setUrl(url);
		datasource
				.setDriverClassName(StringUtils.isEmpty(driver) ? "com.mysql.jdbc.Driver"
						: driver);
		datasource.setUsername(username);
		datasource.setPassword(password);
		datasource.setInitialSize(StringUtils.isEmpty(minSize) ? 20 : Integer
				.parseInt(minSize));
		datasource.setMinIdle(StringUtils.isEmpty(minSize) ? 20 : Integer
				.parseInt(minSize));
		datasource.setMaxActive(StringUtils.isEmpty(maxSize) ? 100 : Integer
				.parseInt(maxSize));
		datasource.setMaxPoolPreparedStatementPerConnectionSize(StringUtils
				.isEmpty(maxPoolPreparedStatementPerConnectionSize) ? 20
				: Integer.parseInt(maxPoolPreparedStatementPerConnectionSize));
	}

	public DruidDB(String url, String username, String password, int minSize,
			int maxSize) {
		this(null, url, "com.mysql.jdbc.Driver", username, password, minSize,
				minSize, maxSize, 20);
	}

	public DruidDB(SQLFormat sqlFormat, String url, String driverClass,
			String username, String password, int initSize, int minSize,
			int maxSize, int maxPoolPreparedStatementPerConnectionSize) {
		super(sqlFormat);
		datasource = new DruidDataSource();
		datasource.setUrl(url);
		datasource.setDriverClassName(driverClass);
		datasource.setUsername(username);
		datasource.setPassword(password);
		datasource.setInitialSize(initSize);
		datasource.setMinIdle(minSize);
		datasource.setMaxActive(maxSize);
		datasource
				.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
	}

	public Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}

	public void close() throws Exception {
		datasource.close();
	}

}
