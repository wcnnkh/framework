package io.basc.framework.jdbc.support;

import java.sql.Connection;
import java.sql.SQLException;

import io.basc.framework.jdbc.ConnectionFactory;
import io.basc.framework.jdbc.JdbcOperations;
import io.basc.framework.jdbc.ResultSetMapper;
import io.basc.framework.jdbc.transaction.SqlTransactionUtils;
import io.basc.framework.orm.EntityMapper;
import io.basc.framework.util.Assert;

public class DefaultJdbcOperations implements JdbcOperations {
	private final ConnectionFactory connectionFactory;
	private final EntityMapper mapper;

	public DefaultJdbcOperations(ConnectionFactory connectionFactory) {
		this(connectionFactory, new ResultSetMapper());
	}

	public DefaultJdbcOperations(ConnectionFactory connectionFactory, EntityMapper mapper) {
		Assert.requiredArgument(connectionFactory != null, "connectionFactory");
		Assert.requiredArgument(mapper != null, "mapper");
		this.connectionFactory = connectionFactory;
		this.mapper = mapper;
	}

	@Override
	public EntityMapper getMapper() {
		return mapper;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return SqlTransactionUtils.getTransactionConnection(connectionFactory);
	}
}
