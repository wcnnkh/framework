package io.basc.framework.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.basc.framework.jdbc.SimpleSql;
import io.basc.framework.jdbc.Sql;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

public abstract class AbstractDataBase implements Database {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String username;
	private final String password;
	private final String driverClass;

	public AbstractDataBase(String username, String password, String driverClass) {
		this.username = username;
		this.password = password;
		this.driverClass = driverClass;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getCreateSql(String database) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE DATABASE IF NOT EXISTS `").append(database).append("`");
		return sb.toString();
	}

	public Connection getConnection() throws SQLException {
		try {
			Class.forName(getDriverClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return DriverManager.getConnection(getUrl(), getUsername(), getPassword());
	}

	public void create(String database) {
		Sql sql = new SimpleSql(getCreateSql(database));
		logger.info(sql.toString());
		consume(sql, (e) -> e.execute());
	}

	public String getDriverClassName() {
		return driverClass;
	}
}
