package scw.db;

import scw.db.cache.CacheManager;
import scw.db.database.DataBase;
import scw.mq.queue.Queue;
import scw.orm.sql.GeneratorService;
import scw.sql.ConnectionFactory;

public interface DBConfig extends ConnectionFactory {
	DataBase getDataBase();

	String getSannerTablePackage();

	Queue<AsyncExecute> getAsyncQueue();

	CacheManager getCacheManager();

	GeneratorService getGeneratorService();
}