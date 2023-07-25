package io.basc.framework.db;

import io.basc.framework.jdbc.ConnectionFactory;
import io.basc.framework.jdbc.template.JdbcTemplate;
import io.basc.framework.jdbc.template.dialect.SqlDialect;
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

	public Database getDatabase(Class<?> entityClass) {
		return databaseRegistry.find(entityClass);
	}

	public Database(ConnectionFactory connectionFactory, SqlDialect dialect) {
		super(connectionFactory, dialect);
	}
}
