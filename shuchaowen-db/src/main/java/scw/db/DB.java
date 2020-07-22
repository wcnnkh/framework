package scw.db;

import scw.sql.Sql;
import scw.sql.SqlOperations;
import scw.sql.orm.EntityOperations;

public interface DB extends EntityOperations, SqlOperations {
	void createTable(Class<?> tableClass, boolean registerManager);

	void createTable(Class<?> tableClass, String tableName, boolean registerManager);

	void createTable(String packageName, boolean registerManager);

	void asyncExecute(AsyncExecute asyncExecute);

	void asyncSave(Object... objs);

	void asyncUpdate(Object... objs);

	void asyncDelete(Object... objs);

	void asyncSaveOrUpdate(Object... objs);

	void asyncExecute(Sql... sqls);
}
