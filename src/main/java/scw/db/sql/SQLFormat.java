package scw.db.sql;

import scw.db.TableInfo;

public interface SQLFormat {
	SQL toCreateTableSql(TableInfo tableInfo, String tableName);
	
	SQL toInsertSql(Object obj);
	SQL toInsertSql(Object obj, TableInfo tableInfo, String tableName);

	SQL toUpdateSql(Object obj);
	SQL toUpdateSql(Object obj, TableInfo tableInfo, String tableName);
	
	SQL toSaveOrUpdateSql(Object obj);
	SQL toSaveOrUpdateSql(Object obj, TableInfo tableInfo, String tableName);

	SQL toDeleteSql(Object bean);
	SQL toDeleteSql(Object bean, TableInfo tableInfo, String tableName);

	SQL toSelectByIdSql(TableInfo tableInfo, String tableName, Object... params);
	
	SQL toIncrSql(Object obj, String fieldName, double limit, Double maxValue);
	SQL toIncrSql(Object obj, TableInfo tableInfo, String tableName, String fieldName, double limit, Double maxValue);
	
	SQL toDecrSql(Object obj, String fieldName, double limit, Double minValue);
	SQL toDecrSql(Object obj, TableInfo tableInfo, String tableName, String fieldName, double limit, Double minValue);
}
