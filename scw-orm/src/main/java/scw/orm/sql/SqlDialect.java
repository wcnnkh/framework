package scw.orm.sql;

import java.util.Collection;

import scw.convert.TypeDescriptor;
import scw.mapper.Field;
import scw.orm.ObjectKeyFormat;
import scw.sql.Sql;

public interface SqlDialect extends ObjectKeyFormat, TableResolver {
	SqlType getSqlType(Class<?> javaType);

	default Object toDataBaseValue(Object value) {
		return toDataBaseValue(value, TypeDescriptor.forObject(value));
	}

	Object toDataBaseValue(Object value, TypeDescriptor sourceType);

	Sql toCreateTableSql(TableStructure tableStructure) throws SqlDialectException;
	
	default Sql toCreateTableSql(String tableName, Class<?> entityClass) throws SqlDialectException{
		return toCreateTableSql(resolve(entityClass).rename(tableName));
	}
	
	Sql toSelectByIdsSql(TableStructure tableStructure, Object... ids) throws SqlDialectException;

	default Sql toSelectByIdsSql(String tableName, Class<?> entityClass, Object... ids) throws SqlDialectException{
		return toSelectByIdsSql(resolve(entityClass).rename(tableName), ids);
	}
	
	<T> Sql save(TableStructure tableStructure, T entity) throws SqlDialectException;

	default <T> Sql save(String tableName, Class<? extends T> entityClass, T entity) throws SqlDialectException{
		return save(resolve(entityClass).rename(tableName), entity);
	}
	
	<T> Sql delete(TableStructure tableStructure, T entity) throws SqlDialectException;

	default <T> Sql delete(String tableName, Class<? extends T> entityClass, T entity) throws SqlDialectException{
		return delete(resolve(entityClass).rename(tableName), entity);
	}
	
	Sql deleteById(TableStructure tableStructure, Object... ids) throws SqlDialectException;
	
	default Sql deleteById(String tableName, Class<?> entityClass, Object... ids) throws SqlDialectException{
		return deleteById(resolve(entityClass).rename(tableName), ids);
	}
	
	<T> Sql update(TableStructure tableStructure, T entity) throws SqlDialectException;

	default <T> Sql update(String tableName, Class<? extends T> entityClass, T entity) throws SqlDialectException{
		return update(resolve(entityClass).rename(tableName), entity);
	}
	
	<T> Sql toSaveOrUpdateSql(TableStructure tableStructure, T entity) throws SqlDialectException;

	default <T> Sql toSaveOrUpdateSql(String tableName, Class<? extends T> entityClass, T entity) throws SqlDialectException{
		return update(resolve(entityClass).rename(tableName), entity);
	}

	Sql toLastInsertIdSql(String tableName) throws SqlDialectException;

	PaginationSql toPaginationSql(Sql sql, long start, long limit) throws SqlDialectException;
	
	Sql getInIds(TableStructure tableStructure, Object[] primaryKeys, Collection<?> inPrimaryKeys)
			throws SqlDialectException;

	default Sql getInIds(String tableName, Class<?> entityClass, Object[] primaryKeys, Collection<?> inPrimaryKeys)
			throws SqlDialectException{
		return getInIds(resolve(entityClass).rename(tableName), primaryKeys, inPrimaryKeys);
	}

	/**
	 * 复制表结构
	 * 
	 * @param newTableName
	 * @param oldTableName
	 * @return
	 */
	Sql toCopyTableStructureSql(Class<?> entityClass, String newTableName, String oldTableName)
			throws SqlDialectException;
	
	Sql toMaxIdSql(TableStructure tableStructure, Field field) throws SqlDialectException;

	default Sql toMaxIdSql(Class<?> clazz, String tableName, Field field) throws SqlDialectException{
		return toMaxIdSql(resolve(clazz).rename(tableName), field);
	}

	TableStructureMapping getTableStructureMapping(Class<?> clazz, String tableName);
}
