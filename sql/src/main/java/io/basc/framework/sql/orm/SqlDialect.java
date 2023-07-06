package io.basc.framework.sql.orm;

import io.basc.framework.data.repository.Operation;
import io.basc.framework.data.repository.Repository;
import io.basc.framework.sql.Sql;
import io.basc.framework.util.Elements;

public interface SqlDialect extends TableMapper, SqlTypeFactory {
	Elements<Sql> toCreateTableSql(TableMapping<?> tableMapping, String tableName) throws SqlDialectException;

	Sql toCopyTableStructureSql(TableMapping<?> tableMapping, String newTableName, String oldTableName)
			throws SqlDialectException;

	Sql toCountSql(Sql sql) throws SqlDialectException;

	Sql toSql(Operation operation);

	Sql toLastInsertIdSql(Repository repository) throws SqlDialectException;

	Sql toLimitSql(Sql sql, long start, long limit) throws SqlDialectException;
}
