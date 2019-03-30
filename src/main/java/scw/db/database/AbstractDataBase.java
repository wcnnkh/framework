package scw.db.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import scw.common.utils.XUtils;
import scw.db.sql.SimpleSql;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.sql.SqlException;
import scw.sql.SqlUtils;

public abstract class AbstractDataBase implements DataBase {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private final String username;
	private final String password;
	private final DataBaseType dataBaseType;

	public AbstractDataBase(String username, String password, DataBaseType dataBaseType) {
		this.username = username;
		this.password = password;
		this.dataBaseType = dataBaseType;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public DataBaseType getDataBaseType() {
		return dataBaseType;
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
		Connection connection = null;
		String sql = getCreateSql(database);
		logger.debug(sql);
		try {
			connection = getConnection();
			SqlUtils.execute(connection, new SimpleSql(sql));
		} catch (SQLException e) {
			throw new SqlException(e);
		} finally {
			XUtils.close(connection);
		}
	}

}
