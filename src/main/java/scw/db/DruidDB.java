package scw.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import com.alibaba.druid.pool.DruidDataSource;

import scw.beans.annotaion.Bean;
import scw.beans.annotaion.Destroy;
import scw.db.database.DataBase;
import scw.sql.orm.cache.Cache;

@Bean(proxy = false)
public class DruidDB extends DB {
	private DruidDataSource datasource;
	private DataBase dataBase;
	private Cache cache;

	/**
	 * @param propertiesFilePath
	 */
	public DruidDB(String propertiesFilePath) {
		this(null, propertiesFilePath);
	}

	@Override
	public DataBase getDataBase() {
		return dataBase;
	}
	
	@Override
	protected Cache getCache() {
		return cache;
	}

	public DruidDB(Cache cache, String propertiesFilePath) {
		this.cache = cache;
		datasource = new DruidDataSource();
		DBUtils.loadProperties(datasource, propertiesFilePath);
		if (!datasource.isPoolPreparedStatements()) {// 如果配置文件中没有开启psCache
			datasource.setMaxPoolPreparedStatementPerConnectionSize(20);
		}

		datasource.setRemoveAbandoned(false);

		this.dataBase = DBUtils.automaticRecognition(datasource.getDriverClassName(), datasource.getUrl(),
				datasource.getUsername(), datasource.getPassword());
		dataBase.create();
	}

	public Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}

	@Destroy
	public void close() throws Exception {
		datasource.close();
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			DriverManager.deregisterDriver(drivers.nextElement());
		}
	}

}
