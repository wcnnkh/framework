package scw.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import scw.beans.annotaion.Destroy;
import scw.db.database.DataBase;
import scw.sql.orm.cache.Cache;

/**
 * 只在能java8中使用 除非你在pom引入你需要的版本，并排除本项目自带的版本
 * 
 * @author shuchaowen
 *
 */
public class HikariCPDB extends DB {
	private HikariDataSource hds;
	private DataBase dataBase;
	private Cache cache;

	public HikariCPDB(String propertiesFile) {
		this(null, propertiesFile);
	}

	@Override
	public Cache getCache() {
		return cache;
	}

	@Override
	public DataBase getDataBase() {
		return dataBase;
	}

	public HikariCPDB(Cache cache, String propertiesFile) {
		HikariConfig config = new HikariConfig();
		DBUtils.loadProperties(config, propertiesFile);

		this.dataBase = DBUtils.automaticRecognition(config.getDriverClassName(), config.getJdbcUrl(),
				config.getUsername(), config.getPassword());
		dataBase.create();

		hds = new HikariDataSource(config);
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
	}

}
