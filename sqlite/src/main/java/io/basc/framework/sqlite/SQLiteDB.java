package io.basc.framework.sqlite;

import io.basc.framework.db.DefaultDB;
import io.basc.framework.env.Environment;
import io.basc.framework.env.Sys;
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

	/**
	 * 在工作目录下创建一个指定名称的数据库
	 * 
	 * @see Sys#env
	 * @see Environment#getWorkPath()
	 * @param name
	 * @return
	 */
	public static SQLiteDB create(String name) {
		return new SQLiteDB(Sys.env.getWorkPath() + "/" + name + ".db");
	}
}
