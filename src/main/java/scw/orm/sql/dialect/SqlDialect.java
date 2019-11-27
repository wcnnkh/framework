package scw.orm.sql.dialect;

import java.util.Collection;

import scw.sql.Sql;

public interface SqlDialect {
	Sql toCreateTableSql(Class<?> clazz, String tableName) throws Exception;

	Sql toInsertSql(Object obj, Class<?> clazz, String tableName) throws Exception;

	Sql toUpdateSql(Object obj, Class<?> clazz, String tableName) throws Exception;

	Sql toSaveOrUpdateSql(Object obj, Class<?> clazz, String tableName) throws Exception;

	Sql toDeleteSql(Object obj, Class<?> clazz, String tableName) throws Exception;

	Sql toDeleteByIdSql(Class<?> clazz, String tableName, Object[] parimayKeys) throws Exception;

	Sql toSelectByIdSql(Class<?> clazz, String tableName, Object[] ids) throws Exception;

	Sql toSelectInIdSql(Class<?> clazz, String tableName, Object[] ids, Collection<?> inIdList) throws Exception;

	PaginationSql toPaginationSql(Sql sql, long page, int limit) throws Exception;

	/**
	 * 复制表结构
	 * 
	 * @param newTableName
	 * @param oldTableName
	 * @return
	 */
	Sql toCopyTableStructureSql(String newTableName, String oldTableName) throws Exception;

	Sql toLastInsertIdSql(String tableName) throws Exception;

	Sql toMaxIdSql(Class<?> clazz, String tableName, String idField) throws Exception;
}
