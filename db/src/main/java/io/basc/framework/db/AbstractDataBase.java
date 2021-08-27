package io.basc.framework.db;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.sql.DefaultSqlStatementProcessor;
import io.basc.framework.sql.SimpleSql;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.SqlException;
import io.basc.framework.sql.SqlUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class AbstractDataBase implements DataBase {
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
		execute(new SimpleSql(getCreateSql(database)));
	}

	public void execute(Sql sql) {
		Connection connection = null;
		logger.info(sql.toString());
		try {
			connection = getConnection();
			SqlUtils.prepare(this, sql, new DefaultSqlStatementProcessor()).execute();
		} catch (SQLException e) {
			throw new SqlException(e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getDriverClassName() {
		return driverClass;
	}
}
