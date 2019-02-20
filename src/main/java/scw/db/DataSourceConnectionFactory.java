package scw.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import scw.sql.ConnectionFactory;

public class DataSourceConnectionFactory implements ConnectionFactory {
	private final DataSource dataSource;

	public DataSourceConnectionFactory(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

}
