package scw.db;

import java.sql.Connection;
import java.sql.SQLException;

import scw.core.instance.annotation.Configuration;
import scw.db.database.DataBase;
import scw.sql.orm.cache.CacheManager;
import scw.sql.orm.support.generation.GeneratorService;
import scw.util.queue.MessageQueue;

@Configuration(order=Integer.MIN_VALUE, value=DB.class)
public class DefaultDB extends AbstractDB {
	private final DBConfig dbConfig;

	public DefaultDB(DBConfig dbConfig) {
		this.dbConfig = dbConfig;
	}

	public final DBConfig getDbConfig() {
		return dbConfig;
	}

	public final DataBase getDataBase() {
		return dbConfig.getDataBase();
	}

	public final Connection getConnection() throws SQLException {
		return dbConfig.getConnection();
	}

	public final String getSannerTablePackage() {
		return dbConfig.getSannerTablePackage();
	}

	public final MessageQueue<AsyncExecute> getAsyncQueue() {
		return dbConfig.getAsyncQueue();
	}

	public final CacheManager getCacheManager() {
		return dbConfig.getCacheManager();
	}

	@Override
	public GeneratorService getGeneratorService() {
		return dbConfig.getGeneratorService();
	}
}
