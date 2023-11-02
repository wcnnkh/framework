package io.basc.framework.jdbc.template;

public class Database extends JdbcTemplate {

	public Database(DatabaseConnectionFactory databaseConnectionFactory) {
		super(databaseConnectionFactory, databaseConnectionFactory.getDatabaseDialect());
	}

	@Override
	public DatabaseConnectionFactory getConnectionFactory() {
		return (DatabaseConnectionFactory) super.getConnectionFactory();
	}
}
