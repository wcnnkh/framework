package scw.sql.orm.auto;

import scw.sql.orm.ColumnInfo;
import scw.sql.orm.ORMOperations;
import scw.sql.orm.TableInfo;

public interface AutoCreateService {

	void wrapper(ORMOperations ormOperations, Object bean, TableInfo tableInfo, ColumnInfo columnInfo, String tableName, String[] args)
			throws Throwable;

}
