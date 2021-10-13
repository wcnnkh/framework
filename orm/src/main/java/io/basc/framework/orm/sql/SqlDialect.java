package io.basc.framework.orm.sql;

import java.util.Collection;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Field;
import io.basc.framework.orm.ObjectKeyFormat;
import io.basc.framework.sql.Sql;

public interface SqlDialect extends ObjectKeyFormat, TableResolver {
	SqlType getSqlType(Class<?> javaType);

	default Object toDataBaseValue(Object value) {
		return toDataBaseValue(value, TypeDescriptor.forObject(value));
	}

	Object toDataBaseValue(Object value, TypeDescriptor sourceType);

	Collection<Sql> createTable(TableStructure tableStructure) throws SqlDialectException;

	default Collection<Sql> createTable(String tableName, Class<?> entityClass) throws SqlDialectException {
		return createTable(resolve(entityClass).rename(tableName));
	}

	Sql toSelectByIdsSql(TableStructure tableStructure, Object... ids) throws SqlDialectException;

	default Sql toSelectByIdsSql(String tableName, Class<?> entityClass, Object... ids) throws SqlDialectException {
		return toSelectByIdsSql(resolve(entityClass).rename(tableName), ids);
	}

	<T> Sql save(TableStructure tableStructure, T entity) throws SqlDialectException;

	default <T> Sql save(String tableName, Class<? extends T> entityClass, T entity)
			throws SqlDialectException {
		return save(resolve(entityClass).rename(tableName), entity);
	}

	<T> Sql delete(TableStructure tableStructure, T entity, @Nullable T condition) throws SqlDialectException;

	default <T> Sql delete(String tableName, Class<? extends T> entityClass, T entity, @Nullable T condition)
			throws SqlDialectException {
		return delete(resolve(entityClass).rename(tableName), entity, condition);
	}

	Sql deleteById(TableStructure tableStructure, Object... ids) throws SqlDialectException;

	default Sql deleteById(String tableName, Class<?> entityClass, Object... ids) throws SqlDialectException {
		return deleteById(resolve(entityClass).rename(tableName), ids);
	}

	<T> Sql update(TableStructure tableStructure, T entity, @Nullable T condition) throws SqlDialectException;

	default <T> Sql update(String tableName, Class<? extends T> entityClass, T entity, @Nullable T condition)
			throws SqlDialectException {
		return update(resolve(entityClass).rename(tableName), entity, condition);
	}

	<T> Sql toSaveOrUpdateSql(TableStructure tableStructure, T entity)
			throws SqlDialectException;

	default <T> Sql toSaveOrUpdateSql(String tableName, Class<? extends T> entityClass, T entity)
			throws SqlDialectException {
		return toSaveOrUpdateSql(resolve(entityClass).rename(tableName), entity);
	}

	Sql toLastInsertIdSql(String tableName) throws SqlDialectException;

	PaginationSql toPaginationSql(Sql sql, long start, long limit) throws SqlDialectException;

	Sql getInIds(TableStructure tableStructure, Object[] primaryKeys, Collection<?> inPrimaryKeys)
			throws SqlDialectException;

	default Sql getInIds(String tableName, Class<?> entityClass, Object[] primaryKeys, Collection<?> inPrimaryKeys)
			throws SqlDialectException {
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

	default Sql toMaxIdSql(Class<?> clazz, String tableName, Field field) throws SqlDialectException {
		return toMaxIdSql(resolve(clazz).rename(tableName), field);
	}

	TableStructureMapping getTableStructureMapping(Class<?> clazz, String tableName);

	Sql query(TableStructure tableStructure, Object query);

	default <T> Sql query(String tableName, Class<T> queryClass, T query) {
		return query(resolve(queryClass).rename(tableName), query);
	}
	
	/**
	 * 条件<br/>
	 * 
	 * if(condition, true, false)
	 * 
	 * @param condition
	 * @param left
	 * @param right
	 * @return
	 */
	Sql condition(Sql condition, Sql left, Sql right);
	
	/**
	 * 将保存和更新语句合并为保存或更新语句
	 * @param saveSql
	 * @param updateSql
	 * @return
	 */
	Sql saveOrUpdate(Sql saveSql, Sql updateSql);
	
	<T> Sql toSaveIfAbsentSql(TableStructure tableStructure, T entity) throws SqlDialectException;

	default <T> Sql toSaveIfAbsentSql(String tableName, Class<? extends T> entityClass, T entity)
			throws SqlDialectException {
		return toSaveIfAbsentSql(resolve(entityClass).rename(tableName), entity);
	}
}
