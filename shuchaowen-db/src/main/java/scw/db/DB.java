package scw.db;

import scw.db.cache.CacheManager;
import scw.orm.sql.ORMOperations;
import scw.sql.Sql;
import scw.sql.SqlOperations;

public interface DB extends ORMOperations, SqlOperations {
	CacheManager getCacheManager();
	
	void createTable(Class<?> tableClass, boolean registerManager);

	void createTable(Class<?> tableClass, String tableName,
			boolean registerManager);

	void createTable(String packageName, boolean registerManager);

	void asyncExecute(AsyncExecute asyncExecute);

	void asyncSave(Object... objs);

	void asyncUpdate(Object... objs);

	void asyncDelete(Object... objs);
	
	void asyncSaveOrUpdate(Object ...objs);

	void asyncExecute(Sql... sqls);
}
