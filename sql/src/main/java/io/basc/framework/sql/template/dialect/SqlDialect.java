package io.basc.framework.sql.template.dialect;

import io.basc.framework.data.repository.DeleteOperation;
import io.basc.framework.data.repository.InsertOperation;
import io.basc.framework.data.repository.QueryOperation;
import io.basc.framework.data.repository.Repository;
import io.basc.framework.data.repository.UpdateOperation;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.template.SqlDialectException;
import io.basc.framework.sql.template.TableMapper;
import io.basc.framework.sql.template.TableMapping;
import io.basc.framework.util.Elements;

public interface SqlDialect extends TableMapper, SqlTypeFactory {
	Elements<Sql> toCreateTableSql(TableMapping<?> tableMapping, String tableName) throws SqlDialectException;

	Sql toCopyTableStructureSql(TableMapping<?> tableMapping, String newTableName, String oldTableName)
			throws SqlDialectException;

	Sql toCountSql(Sql sql) throws SqlDialectException;

	Sql toSql(InsertOperation operation);

	Sql toSql(UpdateOperation operation);

	Sql toSql(DeleteOperation operation);

	Sql toSql(QueryOperation operation);

	Sql toLastInsertIdSql(Repository repository) throws SqlDialectException;

	Sql toLimitSql(Sql sql, long start, long limit) throws SqlDialectException;
}
