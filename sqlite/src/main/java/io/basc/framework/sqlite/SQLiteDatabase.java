package io.basc.framework.sqlite;

import io.basc.framework.jdbc.template.Database;

public class SQLiteDatabase extends Database {
	public SQLiteDatabase() {
		this(new SQLiteConnectionFactory());
	}

	public SQLiteDatabase(SQLiteConnectionFactory connectionFactory) {
		super(connectionFactory);
	}

	@Override
	public SQLiteConnectionFactory getConnectionFactory() {
		return (SQLiteConnectionFactory) super.getConnectionFactory();
	}
}
