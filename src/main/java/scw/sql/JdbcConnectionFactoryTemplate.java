package scw.sql;

import java.sql.Connection;
import java.sql.SQLException;

import scw.transaction.sql.MultipleConnectionTransactionUtils;

public class JdbcConnectionFactoryTemplate extends JdbcTemplate {
	private ConnectionFactory connectionFactory;

	public JdbcConnectionFactoryTemplate(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public Connection getConnection() throws SQLException {
		return MultipleConnectionTransactionUtils.getCurrentConnection(connectionFactory);
	}
}
