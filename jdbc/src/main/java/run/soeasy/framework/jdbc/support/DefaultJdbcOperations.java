package run.soeasy.framework.jdbc.support;

import java.sql.Connection;
import java.sql.SQLException;

import lombok.Getter;
import run.soeasy.framework.jdbc.ConnectionFactory;
import run.soeasy.framework.jdbc.JdbcOperations;
import run.soeasy.framework.jdbc.ResultSetMapper;
import run.soeasy.framework.jdbc.transaction.SqlTransactionUtils;
import run.soeasy.framework.orm.EntityMapper;
import run.soeasy.framework.util.Assert;

@Getter
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
