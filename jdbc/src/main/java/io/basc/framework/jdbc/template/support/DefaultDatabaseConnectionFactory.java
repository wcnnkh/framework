package io.basc.framework.jdbc.template.support;

import java.sql.Connection;
import java.sql.SQLException;

import io.basc.framework.jdbc.ConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseDialect;
import io.basc.framework.lang.Nullable;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.Assert;
import io.basc.framework.util.element.Elements;

public class DefaultDatabaseConnectionFactory<F extends ConnectionFactory> implements DatabaseConnectionFactory {
	private final F connectionFactory;
	@Nullable
	private final DatabaseDialect databaseDialect;

	public DefaultDatabaseConnectionFactory(F connectionFactory, @Nullable DatabaseDialect databaseDialect) {
		Assert.requiredArgument(connectionFactory != null, "connectionFactory");
		this.connectionFactory = connectionFactory;
		this.databaseDialect = databaseDialect;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return connectionFactory.getConnection();
	}

	@Override
	public String getDatabaseName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Elements<String> getDatabaseNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DatabaseConnectionFactory newDatabaseConnectionFactory(String databaseName) throws UnsupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DatabaseDialect getDatabaseDialect() {
		// TODO Auto-generated method stub
		return null;
	}
}
