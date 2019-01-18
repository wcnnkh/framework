package scw.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSource;

import scw.common.utils.ConfigUtils;
import scw.common.utils.StringUtils;
import scw.db.sql.SQLFormat;

public final class DruidDB extends DB {
	private DruidDataSource datasource;

	public DruidDB(SQLFormat sqlFormat, String propertiesFilePath, String charsetName) {
		this(sqlFormat, ConfigUtils.getProperties(propertiesFilePath, charsetName));
	}

	public DruidDB(SQLFormat sqlFormat, Properties properties) {
		super(sqlFormat);
		String url = getProperties(properties, "jdbcUrl", "url", "host");
		String username = getProperties(properties, "username", "user", "name");
		String password = getProperties(properties, "password", "pwd");
		String minSize = getProperties(properties, "minSize", "initialSize", "min");
		String maxSize = getProperties(properties, "maxSize", "maxActive", "max");
		String driver = getProperties(properties, "driver", "driverClass", "driverClassName");
		String maxPoolPreparedStatementPerConnectionSize = getProperties(properties,
				"maxPoolPreparedStatementPerConnectionSize");

		datasource = new DruidDataSource();
		datasource.setUrl(url);
		datasource.setDriverClassName(StringUtils.isEmpty(driver) ? "com.mysql.jdbc.Driver" : driver);
		datasource.setUsername(username);
		datasource.setPassword(password);
		datasource.setInitialSize(StringUtils.isEmpty(minSize) ? 20 : Integer.parseInt(minSize));
		datasource.setMinIdle(StringUtils.isEmpty(minSize) ? 20 : Integer.parseInt(minSize));
		datasource.setMaxActive(StringUtils.isEmpty(maxSize) ? 100 : Integer.parseInt(maxSize));
		datasource.setMaxPoolPreparedStatementPerConnectionSize(
				StringUtils.isEmpty(maxPoolPreparedStatementPerConnectionSize) ? 20
						: Integer.parseInt(maxPoolPreparedStatementPerConnectionSize));
	}

	private String getProperties(Properties properties, String... key) {
		for (String k : key) {
			if (properties.contains(k)) {
				return properties.get(k).toString();
			}
		}
		return null;
	}

	public DruidDB(String url, String username, String password, int minSize, int maxSize) {
		this(null, url, "com.mysql.jdbc.Driver", username, password, minSize, minSize, maxSize, 20);
	}

	public DruidDB(SQLFormat sqlFormat, String url, String driverClass, String username, String password, int initSize,
			int minSize, int maxSize, int maxPoolPreparedStatementPerConnectionSize) {
		super(sqlFormat);
		datasource = new DruidDataSource();
		datasource.setUrl(url);
		datasource.setDriverClassName(driverClass);
		datasource.setUsername(username);
		datasource.setPassword(password);
		datasource.setInitialSize(initSize);
		datasource.setMinIdle(minSize);
		datasource.setMaxActive(maxSize);
		datasource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
	}

	public Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}

	public void close() throws Exception {
		datasource.close();
	}

}
