package scw.db.support;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import scw.core.Destroy;
import scw.db.DBConfig;
import scw.db.DBUtils;
import scw.db.database.DataBase;

public final class HikariCPDBConfig implements DBConfig, Destroy {
	private HikariDataSource hds;
	private DataBase dataBase;

	@SuppressWarnings("rawtypes")
	public HikariCPDBConfig(Map properties) {
		HikariConfig config = new HikariConfig();
		DBUtils.loadProperties(config, properties);

		this.dataBase = DBUtils.automaticRecognition(config.getDriverClassName(), config.getJdbcUrl(),
				config.getUsername(), config.getPassword());

		dataBase.create();
		hds = new HikariDataSource(config);
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
	}

	public DataBase getDataBase() {
		return dataBase;
	}

}
