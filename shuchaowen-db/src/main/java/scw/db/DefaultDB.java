package scw.db;

import java.sql.Connection;
import java.sql.SQLException;

import scw.sql.ConnectionFactory;
import scw.sql.orm.cache.CacheManager;
import scw.sql.orm.dialect.SqlDialect;
import scw.sql.orm.support.generation.GeneratorService;

public class DefaultDB extends AbstractDB {
	private final ConnectionFactory connectionFactory;
	private final SqlDialect sqlDialect;

	public DefaultDB(ConnectionFactory connectionFactory, SqlDialect sqlDialect) {
		this.connectionFactory = connectionFactory;
		this.sqlDialect = sqlDialect;
	}

	public DefaultDB(ConnectionFactory connectionFactory, SqlDialect sqlDialect, CacheManager cacheManager,
			GeneratorService generatorService) {
		super(cacheManager, generatorService);
		this.connectionFactory = connectionFactory;
		this.sqlDialect = sqlDialect;
	}

	public Connection getConnection() throws SQLException {
		return connectionFactory.getConnection();
	}

	@Override
	public SqlDialect getSqlDialect() {
		return sqlDialect;
	}
}
