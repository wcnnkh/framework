package io.basc.framework.sql.orm;

import java.util.Collection;
import java.util.List;

import io.basc.framework.env.Environment;
import io.basc.framework.env.EnvironmentAware;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.orm.repository.Conditions;
import io.basc.framework.orm.repository.OrderColumn;
import io.basc.framework.sql.Sql;
import io.basc.framework.value.Value;

public interface SqlDialect extends TableMapper, EnvironmentAware {
	SqlType getSqlType(Class<?> javaType);

	Environment getEnvironment();

	Object toDataBaseValue(Value value);

	Collection<Sql> createTable(TableStructure tableStructure) throws SqlDialectException;

	Sql toSelectByIdsSql(TableStructure tableStructure, Object... ids) throws SqlDialectException;

	Sql toSaveSql(TableStructure tableStructure, Object entity) throws SqlDialectException;

	Sql toSaveIfAbsentSql(TableStructure tableStructure, Object entity) throws SqlDialectException;

	Sql toInsertSql(TableStructure tableStructure, Object entity) throws SqlDialectException;

	Sql toDeleteSql(TableStructure tableStructure, Object entity) throws SqlDialectException;

	Sql toDeleteByIdSql(TableStructure tableStructure, Object... ids) throws SqlDialectException;

	Sql toUpdateSql(TableStructure tableStructure, Object entity) throws SqlDialectException;

	<T> Sql toUpdateSql(TableStructure tableStructure, T entity, T oldEntity) throws SqlDialectException;

	/**
	 * 转为更新语句，忽略不能为空但实体中为空的字段
	 * 
	 * @param tableStructure
	 * @param entity
	 * @return
	 * @throws SqlDialectException
	 */
	Sql toUpdatePartSql(TableStructure tableStructure, Object entity) throws SqlDialectException;

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

	Sql toQuerySqlByPrimaryKeys(TableStructure tableStructure, Object query);

	Sql toQuerySqlByIndexs(TableStructure tableStructure, Object query);

	Sql toQuerySql(TableStructure tableStructure, Object query);

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

	Sql toSql(Conditions conditions);

	Sql toSaveSql(TableStructure structure, Collection<? extends Parameter> columns);

	Sql toDeleteSql(TableStructure structure, Conditions conditions);

	Sql toUpdateSql(TableStructure structure, Collection<? extends Parameter> columns, Conditions conditions);

	Sql toSelectSql(TableStructure structure, Conditions conditions, List<? extends OrderColumn> orders);
}
