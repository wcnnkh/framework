package scw.db;

import scw.db.database.DataBase;
import scw.sql.ConnectionFactory;
import scw.sql.orm.cache.CacheManager;
import scw.sql.orm.support.generation.GeneratorService;
import scw.util.queue.MessageQueue;

public interface DBConfig extends ConnectionFactory {
	DataBase getDataBase();

	String getSannerTablePackage();

	MessageQueue<AsyncExecute> getAsyncQueue();

	CacheManager getCacheManager();

	GeneratorService getGeneratorService();
}