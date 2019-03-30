package scw.sql.orm;

import java.util.Collection;

import scw.sql.Sql;

public interface SqlFormat {
	Sql toCreateTableSql(TableInfo tableInfo, String tableName);

	Sql toInsertSql(Object obj, TableInfo tableInfo, String tableName);

	Sql toUpdateSql(Object obj, TableInfo tableInfo, String tableName);

	Sql toSaveOrUpdateSql(Object obj, TableInfo tableInfo, String tableName);

	Sql toDeleteSql(Object bean, TableInfo tableInfo, String tableName);

	Sql toDeleteByIdSql(TableInfo tableInfo, String tableName,
			Object[] parimayKeys);

	Sql toSelectByIdSql(TableInfo tableInfo, String tableName, Object[] params);

	Sql toSelectInIdSql(TableInfo tableInfo, String tableName, Object[] params,
			Collection<?> inIdList);

	PaginationSql toPaginationSql(Sql sql, long page, int limit);

	/**
	 * 复制表结构
	 * 
	 * @param newTableName
	 * @param oldTableName
	 * @return
	 */
	Sql toCopyTableStructureSql(String newTableName, String oldTableName);

	Sql toIncrByIdSql(String fieldName, double limit, Double maxValue,
			String tableName, TableInfo tableInfo, Object... params);

	Sql toDecrByIdSql(String fieldName, double limit, Double minValue,
			String tableName, TableInfo tableInfo, Object... params);
}
