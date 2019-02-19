package scw.sql;

import java.sql.Connection;
import java.sql.SQLException;

import scw.transaction.sql.MultipleConnectionTransactionUtils;

public class JdbcTransactionTemplate extends JdbcTemplate {
	private ConnectionFactory connectionFactory;

	public JdbcTransactionTemplate(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public Connection getConnection() throws SQLException {
		return MultipleConnectionTransactionUtils.getCurrentConnection(connectionFactory);
	}
}
