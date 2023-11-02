package io.basc.framework.sqlite;

import java.io.File;
import java.io.IOException;

import io.basc.framework.env.Sys;
import io.basc.framework.util.ObjectUtils;

public class SQLiteFileDatabase extends SQLiteDatabase {

	public SQLiteFileDatabase(String databaseName) {
		this(new File(Sys.getEnv().getWorkPath()), databaseName);
	}

	public SQLiteFileDatabase(File directory, String databaseName) {
		super(new SQLiteFileConnectionFactory(directory, databaseName));
	}

	@Override
	public SQLiteFileConnectionFactory getConnectionFactory() {
		return (SQLiteFileConnectionFactory) super.getConnectionFactory();
	}

	public static void main(String[] args) throws IOException {
		SQLiteFileDatabase database = new SQLiteFileDatabase(new File("D:\\sqlite_test"), "test.db");
		System.out.println(database.getConnectionFactory().getDatabaseName());
		System.out.println(database.getConnectionFactory().getDatabaseNames());
		System.out.println(database.getConnectionFactory().getConnectionFactory().getDataSource().getDatabaseName());
		System.out.println("------");
		database.query(Object[].class, "select * from user").getElements()
				.forEach((e) -> System.out.println(ObjectUtils.toString(e)));
	}
}
