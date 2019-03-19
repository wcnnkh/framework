package scw.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import com.alibaba.druid.pool.DruidDataSource;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

import scw.beans.annotaion.Bean;
import scw.beans.annotaion.Destroy;
import scw.sql.orm.SqlFormat;
import scw.sql.orm.cache.Cache;

@Bean(proxy = false)
public class DruidDB extends DB {
	private DruidDataSource datasource;

	/**
	 * @param propertiesFilePath
	 */
	public DruidDB(SqlFormat sqlFormat, String propertiesFilePath) {
		this(null, sqlFormat, propertiesFilePath);
	}

	public DruidDB(Cache cache, SqlFormat sqlFormat, String propertiesFilePath) {
		super(sqlFormat, cache);
		datasource = new DruidDataSource();
		DBUtils.loadProperties(datasource, propertiesFilePath);
		if (!datasource.isPoolPreparedStatements()) {// 如果配置文件中没有开启psCache
			datasource.setMaxPoolPreparedStatementPerConnectionSize(20);
		}
	}

	public Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}

	@Destroy
	public void close() throws Exception {
		datasource.close();

		AbandonedConnectionCleanupThread.uncheckedShutdown();

		Enumeration<Driver> drivers = DriverManager.getDrivers();
		if (drivers != null) {
			while (drivers.hasMoreElements()) {
				DriverManager.deregisterDriver(drivers.nextElement());
			}
		}
	}

}
