package io.basc.framework.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import io.basc.framework.jdbc.transaction.SqlTransactionUtils;
import io.basc.framework.orm.EntityMapper;
import io.basc.framework.util.Assert;

public class DefaultSqlOperations implements JdbcOperations {
	private ConnectionFactory connectionFactory;
	private final EntityMapper mapper;

	public DefaultSqlOperations(ConnectionFactory connectionFactory) {
		this(connectionFactory, new ResultSetMapper());
	}

	public DefaultSqlOperations(ConnectionFactory connectionFactory, EntityMapper mapper) {
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
