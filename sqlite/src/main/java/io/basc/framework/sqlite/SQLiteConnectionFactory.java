package io.basc.framework.sqlite;

import org.sqlite.SQLiteDataSource;

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
		this(dataSource, new SQLiteDialect());
	}

	protected SQLiteConnectionFactory(@NonNull SQLiteDataSource dataSource, DatabaseDialect databaseDialect) {
		super(dataSource, databaseDialect);
	}

	@Override
	public String getDatabaseName() {
		String databaseName = super.getDatabaseName();
		if (databaseName == null) {
			databaseName = getDataSource().getDatabaseName();
		}
		return databaseName;
	}

}
