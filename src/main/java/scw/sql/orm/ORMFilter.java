package scw.sql.orm;

import scw.sql.orm.enums.OperationType;

public interface ORMFilter {
	boolean doFilter(OperationType operationType, ORMOperations ormOperations, TableInfo tableInfo, String tableName,
			Object bean) throws Throwable;
}
