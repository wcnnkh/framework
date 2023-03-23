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
	Sql condition(Sql condition, Sql left, Sql right);

	Collection<Sql> createTable(TableStructure tableStructure) throws SqlDialectException;

	Environment getEnvironment();

	Sql getInIds(TableStructure tableStructure, Object[] primaryKeys, Collection<?> inPrimaryKeys)
			throws SqlDialectException;

	SqlType getSqlType(Class<?> javaType);

	TableStructureMapping getTableStructureMapping(TableStructure tableStructure);

	Sql toCopyTableStructureSql(Class<?> entityClass, String newTableName, String oldTableName)
			throws SqlDialectException;

	Sql toCountSql(Sql sql) throws SqlDialectException;

	Object toDataBaseValue(Value value);

	Sql toDeleteByIdSql(TableStructure tableStructure, Object... ids) throws SqlDialectException;

	Sql toDeleteSql(TableStructure structure, Conditions conditions);

	Sql toDeleteSql(TableStructure tableStructure, Object entity) throws SqlDialectException;

	Sql toLastInsertIdSql(TableStructure tableStructure) throws SqlDialectException;

	Sql toLimitSql(Sql sql, long start, long limit) throws SqlDialectException;

	Sql toMaxIdSql(TableStructure tableStructure, Field field) throws SqlDialectException;

	Sql toQuerySql(TableStructure tableStructure, Object query);

	Sql toQuerySqlByIndexs(TableStructure tableStructure, Object query);

	Sql toQuerySqlByPrimaryKeys(TableStructure tableStructure, Object query);

	Sql toSaveColumnsSql(TableStructure structure, Collection<? extends Parameter> columns);

	Sql toSaveIfAbsentSql(TableStructure tableStructure, Object entity) throws SqlDialectException;

	Sql toSaveSql(TableStructure tableStructure, Object entity) throws SqlDialectException;

	Sql toSelectByIdsSql(TableStructure tableStructure, Object... ids) throws SqlDialectException;

	Sql toSelectSql(TableStructure structure, Conditions conditions, List<? extends OrderColumn> orders);

	Sql toSql(Conditions conditions);

	Sql toUpdatePartSql(TableStructure tableStructure, Object entity) throws SqlDialectException;

	Sql toUpdateSql(TableStructure structure, Collection<? extends Parameter> columns, Conditions conditions);

	Sql toUpdateSql(TableStructure tableStructure, Object entity) throws SqlDialectException;

	<T> Sql toUpdateSql(TableStructure tableStructure, T entity, T oldEntity) throws SqlDialectException;
}
