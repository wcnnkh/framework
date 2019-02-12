package scw.db.sql;

import java.util.Collection;
import java.util.Map;

import scw.database.TableInfo;
import scw.sql.Sql;

public interface SqlFormat {
	Sql toCreateTableSql(TableInfo tableInfo, String tableName);

	Sql toInsertSql(Object obj, TableInfo tableInfo, String tableName);

	Sql toUpdateSql(Object obj, TableInfo tableInfo, String tableName);

	Sql toUpdateSql(TableInfo tableInfo, String tableName, Map<String, Object> valueMap, Object[] params);

	Sql toSaveOrUpdateSql(Object obj, TableInfo tableInfo, String tableName);

	Sql toDeleteSql(Object bean, TableInfo tableInfo, String tableName);

	Sql toDeleteSql(TableInfo tableInfo, String tableName, Object[] params);

	Sql toSelectByIdSql(TableInfo tableInfo, String tableName, Object[] params);

	Sql toSelectInIdSql(TableInfo tableInfo, String tableName, Object[] params, Collection<?> inIdList);

	Sql toIncrSql(Object obj, TableInfo tableInfo, String tableName, String fieldName, double limit, Double maxValue);

	Sql toDecrSql(Object obj, TableInfo tableInfo, String tableName, String fieldName, double limit, Double minValue);

	PaginationSql toPaginationSql(Sql sql, long page, int limit);
	
	/**
	 * 复制表结构
	 * @param newTableName
	 * @param oldTableName
	 * @return
	 */
	Sql toCopyTableStructure(String newTableName, String oldTableName);
}
