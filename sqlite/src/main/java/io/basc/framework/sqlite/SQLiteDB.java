package io.basc.framework.sqlite;

import org.sqlite.SQLiteDataSource;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.beans.factory.config.Configurable;
import io.basc.framework.db.DefaultDB;
import io.basc.framework.env.Sys;
import io.basc.framework.sql.DataSourceConnectionFactory;

public class SQLiteDB extends DefaultDB implements Configurable {

	public SQLiteDB(SQLiteDataSource dataSource) {
		super(new DataSourceConnectionFactory(dataSource), new SQLiteDialect());
	}

	public SQLiteDB(String databasePath) {
		super(new SQLiteConnectionFactory(databasePath), new SQLiteDialect());
	}

	private boolean configured;

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		((Configurable) getMapper()).configure(serviceLoaderFactory);
		this.configured = true;
	}

	public static SQLiteDB create(String name) {
		return new SQLiteDB(Sys.getEnv().getWorkPath() + "/" + name + ".db");
	}

	@Override
	public boolean isConfigured() {
		return this.configured;
	}
}
