package scw.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import scw.data.memcached.Memcached;
import scw.data.redis.Redis;
import scw.db.DB;
import scw.db.DBUtils;
import scw.db.async.AsyncInfo;
import scw.db.cache.LazyCacheManager;
import scw.db.database.DataBase;
import scw.mq.MQ;

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

	public HikariCPDB(LazyCacheManager lazyCacheManager, MQ<AsyncInfo> mq, String queueName) {
		super(lazyCacheManager, mq, queueName);
	}

	public HikariCPDB(Memcached memcached, String cacheKeyPrefix, String queueName, String propertiesFilePath) {
		super(memcached, cacheKeyPrefix, queueName);
		init(propertiesFilePath);
	}

	public HikariCPDB(Memcached memcached, String cacheKeyPrefix, String propertiesFilePath) {
		this(memcached, cacheKeyPrefix, null, propertiesFilePath);
	}

	public HikariCPDB(Memcached memcached, String propertiesFilePath) {
		this(memcached, null, null, propertiesFilePath);
	}

	public HikariCPDB(Redis redis, String cacheKeyPrefix, String queueName, String propertiesFilePath) {
		super(redis, cacheKeyPrefix, queueName);
		init(propertiesFilePath);
	}

	public HikariCPDB(Redis redis, String cacheKeyPrefix, String propertiesFilePath) {
		super(redis, cacheKeyPrefix, null);
		init(propertiesFilePath);
	}

	public HikariCPDB(Redis redis, String propertiesFilePath) {
		this(redis, null, propertiesFilePath);
	}

	public HikariCPDB(String propertiesFilePath) {
		super();
		init(propertiesFilePath);
	}

	public void createDataBase() {
		if (dataBase != null) {
			dataBase.create();
		}
	}

	public Connection getConnection() throws SQLException {
		return hds.getConnection();
	}

	public void destroy() {
		hds.close();
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
