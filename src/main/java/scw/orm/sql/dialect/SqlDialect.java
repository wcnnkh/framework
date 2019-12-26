package scw.orm.sql.dialect;

import java.util.Collection;

import scw.orm.sql.SqlMapper;
import scw.orm.sql.enums.TableStructureResultField;
import scw.sql.Sql;

public interface SqlDialect {
	SqlMapper getSqlMapper();

	String getTableName(Class<?> clazz, String tableName);

	String getTableName(Class<?> clazz, Object obj, String tableName);

	Sql toCreateTableSql(Class<?> clazz, String tableName) throws SqlDialectException;

	Sql toInsertSql(Object obj, Class<?> clazz, String tableName) throws SqlDialectException;

	Sql toUpdateSql(Object obj, Class<?> clazz, String tableName) throws SqlDialectException;

	Sql toSaveOrUpdateSql(Object obj, Class<?> clazz, String tableName) throws SqlDialectException;

	Sql toDeleteSql(Object obj, Class<?> clazz, String tableName) throws SqlDialectException;

	Sql toDeleteByIdSql(Class<?> clazz, String tableName, Object[] parimayKeys) throws SqlDialectException;

	Sql toSelectByIdSql(Class<?> clazz, String tableName, Object[] ids) throws SqlDialectException;

	Sql toSelectInIdSql(Class<?> clazz, String tableName, Object[] primaryKeys, Collection<?> inPrimaryKeys)
			throws SqlDialectException;

	PaginationSql toPaginationSql(Sql sql, long page, int limit) throws SqlDialectException;

	/**
	 * 复制表结构
	 * 
	 * @param newTableName
	 * @param oldTableName
	 * @return
	 */
	Sql toCopyTableStructureSql(String newTableName, String oldTableName) throws SqlDialectException;

	Sql toLastInsertIdSql(String tableName) throws SqlDialectException;

	Sql toMaxIdSql(Class<?> clazz, String tableName, String idField) throws SqlDialectException;

	Sql toTableStructureSql(Class<?> clazz, String tableName,
			Collection<TableStructureResultField> fields);
}
