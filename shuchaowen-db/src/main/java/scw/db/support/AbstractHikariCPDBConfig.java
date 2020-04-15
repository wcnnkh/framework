package scw.db.support;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import scw.db.DBUtils;
import scw.db.database.DataBase;

public abstract class AbstractHikariCPDBConfig extends AbstractDBConfig {
	private HikariDataSource hds;
	private DataBase dataBase;

	static {
		HikariConfig.class.getName();
	}

	@SuppressWarnings("rawtypes")
	public AbstractHikariCPDBConfig(Map properties) {
		super(properties);
		HikariConfig config = new HikariConfig();
		DBUtils.loadProperties(config, properties);

		this.dataBase = DBUtils.automaticRecognition(config.getDriverClassName(), config.getJdbcUrl(),
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

	public void destroy() throws Exception {
		hds.close();
		super.destroy();
	}
}
