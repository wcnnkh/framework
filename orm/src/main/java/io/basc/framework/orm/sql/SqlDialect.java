package io.basc.framework.orm.sql;

import java.util.Collection;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Field;
import io.basc.framework.orm.ObjectKeyFormat;
import io.basc.framework.sql.Sql;

public interface SqlDialect extends ObjectKeyFormat, TableResolver, ConversionServiceAware {
	SqlType getSqlType(Class<?> javaType);

	ConversionService getConversionService();

	default Object toDataBaseValue(Object value) {
		return toDataBaseValue(value, TypeDescriptor.forObject(value));
	}

	Object toDataBaseValue(Object value, TypeDescriptor sourceType);

	Collection<Sql> createTable(TableStructure tableStructure) throws SqlDialectException;

	Sql toSelectByIdsSql(TableStructure tableStructure, Object... ids) throws SqlDialectException;

	<T> Sql save(TableStructure tableStructure, T entity) throws SqlDialectException;

	<T> Sql delete(TableStructure tableStructure, T entity, @Nullable T condition) throws SqlDialectException;

	Sql deleteById(TableStructure tableStructure, Object... ids) throws SqlDialectException;

	<T> Sql update(TableStructure tableStructure, T entity, @Nullable T condition) throws SqlDialectException;

	<T> Sql toSaveOrUpdateSql(TableStructure tableStructure, T entity) throws SqlDialectException;

	Sql toLastInsertIdSql(TableStructure tableStructure) throws SqlDialectException;

	Sql toCountSql(Sql sql) throws SqlDialectException;

	Sql toLimitSql(Sql sql, long start, long limit) throws SqlDialectException;

	Sql getInIds(TableStructure tableStructure, Object[] primaryKeys, Collection<?> inPrimaryKeys)
			throws SqlDialectException;

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

	TableStructureMapping getTableStructureMapping(TableStructure tableStructure);

	Sql query(TableStructure tableStructure, Object query);

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

	<T> Sql toSaveIfAbsentSql(TableStructure tableStructure, T entity) throws SqlDialectException;
}
