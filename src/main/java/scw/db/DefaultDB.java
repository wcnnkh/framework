package scw.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import scw.beans.annotation.Bean;
import scw.db.cache.CacheManager;
import scw.db.database.DataBase;
import scw.mq.queue.Queue;
import scw.sql.orm.ORMFilter;

@Bean(proxy = false)
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

	public final Queue<AsyncExecute> getAsyncQueue() {
		return dbConfig.getAsyncQueue();
	}

	public final CacheManager getCacheManager() {
		return dbConfig.getCacheManager();
	}

	public Collection<ORMFilter> getORMFilter() {
		return dbConfig.getORMFilter();
	}
}
