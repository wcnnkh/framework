package io.basc.framework.jdbc.template;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.Query;
import io.basc.framework.data.repository.DeleteOperation;
import io.basc.framework.data.repository.InsertOperation;
import io.basc.framework.data.repository.QueryOperation;
import io.basc.framework.data.repository.RepositoryException;
import io.basc.framework.data.repository.UpdateOperation;
import io.basc.framework.jdbc.ConnectionFactory;
import io.basc.framework.jdbc.Sql;
import io.basc.framework.jdbc.support.DefaultJdbcOperations;
import io.basc.framework.jdbc.template.dialect.SqlDialect;
import io.basc.framework.lang.Nullable;
import io.basc.framework.orm.EntityOperations;
import io.basc.framework.util.element.Elements;

public class JdbcTemplate extends DefaultJdbcOperations implements EntityOperations {

	public JdbcTemplate(ConnectionFactory connectionFactory, SqlDialect sqlDialect) {
		super(connectionFactory, sqlDialect);
	}

	public SqlDialect getMapper() {
		return (SqlDialect) super.getMapper();
	}

	public Elements<String> getTableNames() {
		return getMapper().getTableNames(operations());
	}

	public long count(Sql sql) {
		Sql countSql = getMapper().toCountSql(sql);
		return query(long.class, countSql).getElements().first();
	}

	public void createTable(Class<?> entityClass) {
		createTable(entityClass, null);
	}

	public void createTable(Class<?> entityClass, @Nullable String tableName) {
		TableMapping<?> tableMapping = getMapper().getMapping(entityClass);
		createTable(tableMapping, tableName);
	}

	public void createTable(TableMapping<?> tableMapping, String tableName) {
		Elements<Sql> sqls = getMapper().toCreateTableSql(tableMapping, tableName);
		for (Sql sql : sqls) {
			execute(sql);
		}
	}

	@Override
	public long delete(DeleteOperation operation) throws RepositoryException {
		Sql sql = getMapper().toSql(operation);
		return update(sql);
	}

	@Override
	public long insert(InsertOperation operation) throws RepositoryException {
		Sql sql = getMapper().toSql(operation);
		return update(sql);
	}

	@Override
	public <T> Query<T> query(TypeDescriptor resultTypeDescriptor, QueryOperation operation)
			throws RepositoryException {
		Sql sql = getMapper().toSql(operation);
		return query(resultTypeDescriptor, sql);
	}

	@Override
	public long update(UpdateOperation operation) throws RepositoryException {
		Sql sql = getMapper().toSql(operation);
		return update(sql);
	}
}
