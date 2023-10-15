package io.basc.framework.db;

import io.basc.framework.jdbc.ConnectionFactory;
import io.basc.framework.jdbc.template.JdbcTemplate;
import io.basc.framework.lang.Nullable;
import io.basc.framework.orm.EntityRepositoryRegistry;

/**
 * 数据库
 * 
 * @author wcnnkh
 *
 */
public class Database extends JdbcTemplate {
	private static EntityRepositoryRegistry<Database> databaseRegistry = new EntityRepositoryRegistry<>();

	public static EntityRepositoryRegistry<Database> getDatabaseRegistry() {
		return databaseRegistry;
	}

	public static Database getDatabase(Class<?> entityClass) {
		return databaseRegistry.find(entityClass);
	}

	private DatabaseProperties databaseProperties;

	public Database(ConnectionFactory connectionFactory, DatabaseDialect dialect) {
		this(connectionFactory, dialect, new DatabaseProperties());
	}

	public Database(ConnectionFactory connectionFactory, DatabaseDialect dialect,
			@Nullable DatabaseProperties databaseProperties) {
		super(connectionFactory, dialect);
		this.databaseProperties = databaseProperties;
	}

	public DatabaseProperties getDatabaseProperties() {
		return databaseProperties;
	}

	public void setDatabaseProperties(DatabaseProperties databaseProperties) {
		this.databaseProperties = databaseProperties;
	}

	@Override
	public DatabaseDialect getMapper() {
		return (DatabaseDialect) super.getMapper();
	}
}
