package scw.sqlite;

import org.sqlite.SQLiteDataSource;

import scw.db.DefaultDB;
import scw.sql.DataSourceConnectionFactory;

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
