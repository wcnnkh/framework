package scw.sql.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import scw.sql.ConnectionFactory;
import scw.sql.JdbcTemplate;
import scw.transaction.sql.MultipleConnectionTransactionUtils;

public class JdbcTransactionTemplate extends JdbcTemplate {
	private ConnectionFactory connectionFactory;

	public JdbcTransactionTemplate(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public Connection getConnection() throws SQLException {
		Connection connection = MultipleConnectionTransactionUtils.getCurrentConnection(connectionFactory);
		if (connection == null) {
			connection = connectionFactory.getConnection();
		}
		return connection;
	}
}
