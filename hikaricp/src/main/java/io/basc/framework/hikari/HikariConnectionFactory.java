package io.basc.framework.hikari;

import com.zaxxer.hikari.HikariDataSource;

import io.basc.framework.jdbc.template.DatabaseDialect;
import io.basc.framework.jdbc.template.support.DataSourceDatabaseConnectionFactory;

public class HikariConnectionFactory extends DataSourceDatabaseConnectionFactory<HikariDataSource> {

	public HikariConnectionFactory(HikariDataSource dataSource, DatabaseDialect databaseDialect) {
		super(dataSource, databaseDialect);
	}
}
