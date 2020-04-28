package scw.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import scw.sql.ConnectionFactory;

public class DefaultConnectionFactory implements ConnectionFactory {
	private final DataSource dataSource;

	public DefaultConnectionFactory(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}
