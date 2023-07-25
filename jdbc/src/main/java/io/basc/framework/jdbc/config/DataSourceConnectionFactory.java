package io.basc.framework.jdbc.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import io.basc.framework.jdbc.ConnectionFactory;

public class DataSourceConnectionFactory implements ConnectionFactory {
	private DataSource dataSource;

	public DataSourceConnectionFactory(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}
