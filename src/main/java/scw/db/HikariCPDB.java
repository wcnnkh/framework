package scw.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import scw.beans.annotation.Destroy;
import scw.common.Base64;
import scw.core.Constants;
import scw.db.database.DataBase;
import scw.memcached.Memcached;
import scw.redis.Redis;

/**
 * 只在能java8中使用 除非你在pom引入你需要的版本，并排除本项目自带的版本
 * 
 * @author shuchaowen
 *
 */
public class HikariCPDB extends DB {
	private HikariDataSource hds;
	private DataBase dataBase;

	@Override
	public DataBase getDataBase() {
		return dataBase;
	}

	private void init(String propertiesFile) {
		HikariConfig config = new HikariConfig();
		DBUtils.loadProperties(config, propertiesFile);

		this.dataBase = DBUtils.automaticRecognition(config.getDriverClassName(), config.getJdbcUrl(),
				config.getUsername(), config.getPassword());
		dataBase.create();

		hds = new HikariDataSource(config);
	}

	public HikariCPDB(String propertiesFile) {
		init(propertiesFile);
	}

	public HikariCPDB(Memcached memcached, String queueName, String propertiesFile) {
		super(memcached, queueName);
		init(propertiesFile);
	}

	public HikariCPDB(Redis redis, String queueName, String propertiesFile) {
		super(redis, queueName);
		init(propertiesFile);
	}

	public HikariCPDB(Redis redis, String propertiesFile) {
		this(redis, Base64.encode(propertiesFile.getBytes(Constants.DEFAULT_CHARSET)), propertiesFile);
		DBUtils.queueNameWarn(logger);
	}

	public HikariCPDB(Memcached memcached, String propertiesFile) {
		this(memcached, Base64.encode(propertiesFile.getBytes(Constants.DEFAULT_CHARSET)), propertiesFile);
		DBUtils.queueNameWarn(logger);
	}

	public void createDataBase() {
		if (dataBase != null) {
			dataBase.create();
		}
	}

	public Connection getConnection() throws SQLException {
		return hds.getConnection();
	}

	@Destroy
	public void close() throws Exception {
		hds.close();
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			DriverManager.deregisterDriver(drivers.nextElement());
		}
		super.close();
	}

}
