package scw.db;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import scw.beans.annotaion.Destroy;
import scw.sql.orm.SqlFormat;
import scw.sql.orm.cache.Cache;

/**
 * 只在能java8中使用 除非你在pom引入你需要的版本，并排除本项目自带的版本
 * 
 * @author shuchaowen
 *
 */
public class HikariCPDB extends DB {
	private HikariDataSource hds;

	public HikariCPDB(SqlFormat sqlFormat, String propertiesFile) {
		this(sqlFormat, null, propertiesFile);
	}

	public HikariCPDB(SqlFormat sqlFormat, Cache cache, String propertiesFile) {
		super(sqlFormat, cache);
		HikariConfig config = new HikariConfig();
		DBUtils.loadProperties(config, propertiesFile);
		hds = new HikariDataSource(config);
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
