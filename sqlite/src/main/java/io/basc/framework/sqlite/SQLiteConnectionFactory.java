package io.basc.framework.sqlite;

import org.sqlite.SQLiteDataSource;

import io.basc.framework.jdbc.support.DataSourceConnectionFactory;
import io.basc.framework.jdbc.template.DatabaseDialect;
import io.basc.framework.jdbc.template.support.DataSourceDatabaseConnectionFactory;
import lombok.NonNull;

public class SQLiteConnectionFactory extends DataSourceDatabaseConnectionFactory<SQLiteDataSource> {

	/**
	 * 使用内存数据库
	 */
	public SQLiteConnectionFactory() {
		this(new SQLiteDataSource());
	}

	public SQLiteConnectionFactory(@NonNull SQLiteDataSource dataSource) {
		super(dataSource, new SQLiteDialect());
	}

	protected SQLiteConnectionFactory(@NonNull DataSourceConnectionFactory<SQLiteDataSource> connectionFactory,
			DatabaseDialect databaseDialect) {
		super(connectionFactory, databaseDialect);
	}

	@Override
	public String getDatabaseName() {
		String databaseName = super.getDatabaseName();
		if (databaseName == null) {
			databaseName = getConnectionFactory().getDataSource().getDatabaseName();
		}
		return databaseName;
	}

}
