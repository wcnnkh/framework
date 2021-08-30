package io.basc.framework.sqlite;

import io.basc.framework.db.DefaultDB;
import io.basc.framework.sql.DataSourceConnectionFactory;

import org.sqlite.SQLiteDataSource;

public class SQLiteDB extends DefaultDB {

	public SQLiteDB(SQLiteDataSource dataSource) {
		super(new DataSourceConnectionFactory(dataSource), new SQLiteDialect());
		setCheckTableChange(false);
	}

	public SQLiteDB(String databasePath) {
		super(new SQLiteConnectionFactory(databasePath), new SQLiteDialect());
		setCheckTableChange(false);
	}
}
