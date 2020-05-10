package scw.sql.support;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import scw.core.instance.annotation.Configuration;
import scw.sql.ConnectionFactory;

@Configuration(order=Integer.MIN_VALUE)
public class DefaultConnectionFactory implements ConnectionFactory {
	private final DataSource dataSource;

	public DefaultConnectionFactory(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}
}
