package scw.orm.sql.dialect;

import java.util.Collection;

import scw.orm.sql.SqlMappingOperations;
import scw.sql.Sql;

public interface SqlDialect {
	Sql toCreateTableSql(SqlMappingOperations sqlMappingOperations, Class<?> clazz, String tableName)
			throws SqlDialectException;

	Sql toInsertSql(SqlMappingOperations sqlMappingOperations, Object obj, Class<?> clazz, String tableName)
			throws SqlDialectException;

	Sql toUpdateSql(SqlMappingOperations sqlMappingOperations, Object obj, Class<?> clazz, String tableName)
			throws SqlDialectException;

	Sql toSaveOrUpdateSql(SqlMappingOperations sqlMappingOperations, Object obj, Class<?> clazz, String tableName)
			throws SqlDialectException;

	Sql toDeleteSql(SqlMappingOperations sqlMappingOperations, Object obj, Class<?> clazz, String tableName)
			throws SqlDialectException;

	Sql toDeleteByIdSql(SqlMappingOperations sqlMappingOperations, Class<?> clazz, String tableName,
			Object[] parimayKeys) throws SqlDialectException;

	Sql toSelectByIdSql(SqlMappingOperations sqlMappingOperations, Class<?> clazz, String tableName, Object[] ids)
			throws SqlDialectException;

	Sql toSelectInIdSql(SqlMappingOperations sqlMappingOperations, Class<?> clazz, String tableName, Object[] ids,
			Collection<?> inIdList) throws SqlDialectException;

	PaginationSql toPaginationSql(SqlMappingOperations sqlMappingOperations, Sql sql, long page, int limit)
			throws SqlDialectException;

	/**
	 * 复制表结构
	 * 
	 * @param newTableName
	 * @param oldTableName
	 * @return
	 */
	Sql toCopyTableStructureSql(SqlMappingOperations sqlMappingOperations, String newTableName, String oldTableName)
			throws SqlDialectException;

	Sql toLastInsertIdSql(SqlMappingOperations sqlMappingOperations, String tableName) throws SqlDialectException;

	Sql toMaxIdSql(SqlMappingOperations sqlMappingOperations, Class<?> clazz, String tableName, String idField)
			throws SqlDialectException;
}
