package scw.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import scw.mapper.Mapper;
import scw.mapper.SimpleMapper;
import scw.sql.transaction.SqlTransactionUtils;

public class DefaultSqlOperations extends DefaultSqlStatementProcessor implements SqlOperations {
	private ConnectionFactory connectionFactory;
	private final Mapper<ResultSet, Throwable> mapper = new SimpleMapper<>();

	public DefaultSqlOperations(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
	
	@Override
	public Mapper<ResultSet, Throwable> getMapper() {
		return mapper;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return SqlTransactionUtils.getTransactionConnection(connectionFactory);
	}
}
