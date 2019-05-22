package scw.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import com.alibaba.druid.pool.DruidDataSource;

import scw.beans.annotation.Bean;
import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.db.database.DataBase;

@Bean(proxy = false)
public class DruidDB extends DB {
	private DruidDataSource datasource;
	private DataBase dataBase;

	@Override
	public DataBase getDataBase() {
		return dataBase;
	}

	public DruidDB(Memcached memcached, String queueName, String propertiesFilePath) {
		super(memcached, queueName);
		init(propertiesFilePath);
	}

	public DruidDB(Memcached memcached, String propertiesFilePath) {
		this(memcached, null, propertiesFilePath);
	}

	public DruidDB(Redis redis, String queueName, String propertiesFilePath) {
		super(redis, queueName);
		init(propertiesFilePath);
	}

	public DruidDB(Redis redis, String propertiesFilePath) {
		this(redis, null, propertiesFilePath);
	}

	private void init(String propertiesFilePath) {
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

	public DruidDB(String propertiesFilePath) {
		init(propertiesFilePath);
	}

	public Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}

	@Override
	public void destroy() {
		datasource.close();
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			try {
				DriverManager.deregisterDriver(drivers.nextElement());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		super.destroy();
	}
}
