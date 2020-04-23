package scw.db;

import scw.db.cache.CacheManager;
import scw.db.database.DataBase;
import scw.orm.sql.GeneratorService;
import scw.sql.ConnectionFactory;
import scw.util.queue.MessageQueue;

public interface DBConfig extends ConnectionFactory {
	DataBase getDataBase();

	String getSannerTablePackage();

	MessageQueue<AsyncExecute> getAsyncQueue();

	CacheManager getCacheManager();

	GeneratorService getGeneratorService();
}