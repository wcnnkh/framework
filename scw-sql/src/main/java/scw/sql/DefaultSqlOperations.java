package scw.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import scw.convert.TypeDescriptor;
import scw.sql.transaction.SqlTransactionUtils;
import scw.util.stream.Processor;

public class DefaultSqlOperations extends DefaultSqlStatementProcessor implements SqlOperations {
	private ConnectionFactory connectionFactory;

	public DefaultSqlOperations(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return SqlTransactionUtils.getTransactionConnection(connectionFactory);
	}
	
	@Override
	public <T> Processor<ResultSet, T, ? extends Throwable> getMapperProcessor(
			TypeDescriptor type) {
		return new DefaultMapperProcessor<T>(type);
	}
}
