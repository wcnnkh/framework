package scw.db.support;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;

import scw.core.Destroy;
import scw.db.DBUtils;
import scw.db.database.DataBase;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public abstract class AbstractHikariCPDBConfig extends AbstractDBConfig
		implements Destroy {
	private HikariDataSource hds;
	private DataBase dataBase;
	
	static{
		HikariConfig.class.getName();
	}

	@SuppressWarnings("rawtypes")
	public AbstractHikariCPDBConfig(Map properties) {
		super(properties);
		HikariConfig config = new HikariConfig();
		DBUtils.loadProperties(config, properties);

		this.dataBase = DBUtils.automaticRecognition(
				config.getDriverClassName(), config.getJdbcUrl(),
				config.getUsername(), config.getPassword());
		dataBase.create();
		hds = new HikariDataSource(config);
	}

	public DataBase getDataBase() {
		return dataBase;
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
}
