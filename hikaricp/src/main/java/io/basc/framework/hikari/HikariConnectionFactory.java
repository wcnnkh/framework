package io.basc.framework.hikari;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;

import io.basc.framework.jdbc.ConnectionFactory;

public class HikariConnectionFactory implements ConnectionFactory {
	private final HikariDataSource dataSource;

	public HikariConnectionFactory(HikariDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public HikariDataSource getDataSource() {
		return dataSource;
	}
}
