package io.basc.framework.sql.orm;

import io.basc.framework.data.repository.DeleteOperation;
import io.basc.framework.data.repository.InsertOperation;
import io.basc.framework.data.repository.SelectOperation;
import io.basc.framework.data.repository.UpdateOperation;
import io.basc.framework.env.Environment;
import io.basc.framework.env.EnvironmentAware;
import io.basc.framework.sql.Sql;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;

public interface SqlDialect extends TableMapper, EnvironmentAware {
	Sql condition(Sql condition, Sql left, Sql right);

	Elements<Sql> toCreateTableSql(TableMapping<? extends Column> tableMapping) throws SqlDialectException;

	Environment getEnvironment();

	SqlType getSqlType(Class<?> javaType);

	TableStructureMapping getTableStructureMapping(TableMapping<? extends Column> tableMapping);

	Sql toCopyTableStructureSql(String newTableName, String oldTableName) throws SqlDialectException;

	Sql toCountSql(Sql sql) throws SqlDialectException;

	Object toDataBaseValue(Value value);

	Sql toDeleteSql(DeleteOperation operation);

	Sql toSelectSql(SelectOperation operation);

	Sql toUpdateSql(UpdateOperation operation);

	Sql toInsertSql(InsertOperation operation);

	Sql toLastInsertIdSql(TableMapping<? extends Column> tableMapping) throws SqlDialectException;

	Sql toLimitSql(Sql sql, long start, long limit) throws SqlDialectException;
}
