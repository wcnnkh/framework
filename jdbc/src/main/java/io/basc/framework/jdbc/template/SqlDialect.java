package io.basc.framework.jdbc.template;

import io.basc.framework.data.repository.DeleteOperation;
import io.basc.framework.data.repository.InsertOperation;
import io.basc.framework.data.repository.QueryOperation;
import io.basc.framework.data.repository.Repository;
import io.basc.framework.data.repository.UpdateOperation;
import io.basc.framework.jdbc.ConnectionOperations;
import io.basc.framework.jdbc.Sql;
import io.basc.framework.orm.EntityMapper;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.util.Elements;

public interface SqlDialect extends EntityMapper, SqlTypeFactory {
	Elements<Sql> toCreateTableSql(EntityMapping<?> tableMapping, String tableName) throws SqlDialectException;

	Sql toCopyTableStructureSql(EntityMapping<?> tableMapping, String newTableName, String oldTableName)
			throws SqlDialectException;

	Sql toCountSql(Sql sql) throws SqlDialectException;

	Sql toSql(InsertOperation operation);

	Sql toSql(UpdateOperation operation);

	Sql toSql(DeleteOperation operation);

	Sql toSql(QueryOperation operation);

	Sql toLastInsertIdSql(Repository repository) throws SqlDialectException;

	Sql toLimitSql(Sql sql, long start, long limit) throws SqlDialectException;

	/**
	 * 获取所有的表名
	 * 
	 * @param operations
	 * @return
	 */
	Elements<String> getTableNames(ConnectionOperations operations);
}
