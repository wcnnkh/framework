package io.basc.framework.hikari;

import io.basc.framework.jdbc.template.Database;
import io.basc.framework.jdbc.template.DatabaseConnectionFactory;

public class HikariDatabase extends Database {

	public HikariDatabase(DatabaseConnectionFactory databaseConnectionFactory) {
		super(databaseConnectionFactory);
	}
	
}
