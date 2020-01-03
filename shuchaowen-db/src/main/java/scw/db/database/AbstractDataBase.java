package scw.db.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.orm.sql.dialect.SqlDialect;
import scw.sql.SimpleSql;
import scw.sql.Sql;
import scw.sql.SqlException;
import scw.sql.SqlUtils;

public abstract class AbstractDataBase implements DataBase {
	private Logger logger = LoggerUtils.getLogger(this.getClass());
	private final String username;
	private final String password;
	private final String driverClass;
	private final SqlDialect sqlDialect;

	public AbstractDataBase(String username, String password, String driverClass, SqlDialect sqlDialect) {
		this.username = username;
		this.password = password;
		this.driverClass = driverClass;
		this.sqlDialect = sqlDialect;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public SqlDialect getSqlDialect() {
		return sqlDialect;
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

		return DriverManager.getConnection(getConnectionURL(), getUsername(), getPassword());
	}

	public void create() {
		create(getDataBase());
	}

	public void create(String database) {
		execute(new SimpleSql(getCreateSql(database)));
	}

	public void execute(Sql sql) {
		Connection connection = null;
		logger.info(SqlUtils.getSqlId(sql));
		try {
			connection = getConnection();
			SqlUtils.execute(connection, sql);
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
