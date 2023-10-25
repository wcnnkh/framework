package io.basc.framework.jdbc.template.dialect;

import io.basc.framework.data.repository.DeleteOperation;
import io.basc.framework.data.repository.InsertOperation;
import io.basc.framework.data.repository.QueryOperation;
import io.basc.framework.data.repository.Repository;
import io.basc.framework.data.repository.UpdateOperation;
import io.basc.framework.jdbc.Sql;
import io.basc.framework.jdbc.template.SqlDialectException;
import io.basc.framework.jdbc.template.TableMapper;
import io.basc.framework.jdbc.template.TableMapping;
import io.basc.framework.util.element.Elements;

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

	/**
	 * 获取创建指定数据库名称的语句
	 * 
	 * @param databaseName
	 * @return
	 */
	Sql getCreateDatabaseSql(String databaseName);
}
