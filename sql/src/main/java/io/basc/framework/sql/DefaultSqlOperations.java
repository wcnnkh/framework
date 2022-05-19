package io.basc.framework.sql;

import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.mapper.SimpleObjectMapper;
import io.basc.framework.sql.transaction.SqlTransactionUtils;
import io.basc.framework.util.Assert;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DefaultSqlOperations extends DefaultSqlStatementProcessor
		implements SqlOperations {
	private ConnectionFactory connectionFactory;
	private final ObjectMapper<ResultSet, Throwable> mapper;

	public DefaultSqlOperations(ConnectionFactory connectionFactory) {
		this(connectionFactory, new SimpleObjectMapper<>());
	}

	public DefaultSqlOperations(ConnectionFactory connectionFactory,
			ObjectMapper<ResultSet, Throwable> mapper) {
		Assert.requiredArgument(connectionFactory != null, "connectionFactory");
		Assert.requiredArgument(mapper != null, "mapper");
		this.connectionFactory = connectionFactory;
		this.mapper = mapper;
	}

	@Override
	public ObjectMapper<ResultSet, Throwable> getMapper() {
		return mapper;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return SqlTransactionUtils.getTransactionConnection(connectionFactory);
	}
}
