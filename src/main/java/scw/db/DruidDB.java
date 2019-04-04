package scw.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import com.alibaba.druid.pool.DruidDataSource;

import scw.beans.annotation.Bean;
import scw.common.Base64;
import scw.common.Constants;
import scw.db.database.DataBase;
import scw.memcached.Memcached;
import scw.redis.Redis;

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

	public DruidDB(Redis redis, String queueName, String propertiesFilePath) {
		super(redis, queueName);
		init(propertiesFilePath);
	}

	public DruidDB(Redis redis, String propertiesFile) {
		this(redis, Base64.encode(propertiesFile.getBytes(Constants.DEFAULT_CHARSET)), propertiesFile);
	}

	public DruidDB(Memcached memcached, String propertiesFile) {
		this(memcached, Base64.encode(propertiesFile.getBytes(Constants.DEFAULT_CHARSET)), propertiesFile);
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

	public void close() throws Exception {
		datasource.close();
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			DriverManager.deregisterDriver(drivers.nextElement());
		}
		super.close();
	}

}
