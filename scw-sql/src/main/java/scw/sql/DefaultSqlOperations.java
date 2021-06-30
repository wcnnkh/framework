package scw.sql;

import java.sql.Connection;
import java.sql.SQLException;

import scw.sql.transaction.SqlTransactionUtils;

public class DefaultSqlOperations extends DefaultSqlStatementProcessor implements SqlOperations {
	private ConnectionFactory connectionFactory;

	public DefaultSqlOperations(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return SqlTransactionUtils.getTransactionConnection(connectionFactory);
	}
}
