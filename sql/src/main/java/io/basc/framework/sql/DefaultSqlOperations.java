package io.basc.framework.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.basc.framework.mapper.ObjectMapper;
import io.basc.framework.sql.transaction.SqlTransactionUtils;
import io.basc.framework.util.Assert;

public class DefaultSqlOperations extends DefaultSqlStatementProcessor implements SqlOperations {
	private ConnectionFactory connectionFactory;
	private final ObjectMapper<ResultSet, SQLException> mapper;

	public DefaultSqlOperations(ConnectionFactory connectionFactory) {
		this(connectionFactory, new ResultSetMapper());
	}

	public DefaultSqlOperations(ConnectionFactory connectionFactory, ObjectMapper<ResultSet, SQLException> mapper) {
		Assert.requiredArgument(connectionFactory != null, "connectionFactory");
		Assert.requiredArgument(mapper != null, "mapper");
		this.connectionFactory = connectionFactory;
		this.mapper = mapper;
	}

	@Override
	public ObjectMapper<ResultSet, SQLException> getMapper() {
		return mapper;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return SqlTransactionUtils.getTransactionConnection(connectionFactory);
	}
}
