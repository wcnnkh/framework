package scw.db;

import java.sql.SQLException;

import scw.db.async.MultipleOperation;
import scw.db.async.OperationBean;
import scw.sql.Sql;
import scw.sql.SqlOperations;
import scw.sql.orm.ORMOperations;

public interface DB extends ORMOperations, SqlOperations {
	void createTable(Class<?> tableClass, boolean registerManager);
	
	void createTable(Class<?> tableClass, String tableName, boolean registerManager);
	
	void createTable(String packageName, boolean registerManager);
	
	void asyncSave(Object... objs);

	void asyncUpdate(Object... objs);

	void asyncDelete(Object... objs);

	void asyncSaveOrUpdate(Object... objs);

	void asyncExecute(OperationBean... operationBeans);

	void asyncExecute(MultipleOperation multipleOperation);

	void asyncExecute(Sql... sql);
	
	void executeSqlByFile(String filePath);

	void executeSqlByFileLine(String filePath, String ignoreStartsWith) throws SQLException;

	void executeSqlsByFileLine(String filePath) throws SQLException;
}
