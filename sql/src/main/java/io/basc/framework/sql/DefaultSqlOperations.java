package io.basc.framework.sql;

import io.basc.framework.mapper.Mapper;
import io.basc.framework.mapper.SimpleMapper;
import io.basc.framework.sql.transaction.SqlTransactionUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

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
