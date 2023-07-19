package io.basc.framework.sql.template;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.Query;
import io.basc.framework.data.repository.DeleteOperation;
import io.basc.framework.data.repository.InsertOperation;
import io.basc.framework.data.repository.QueryOperation;
import io.basc.framework.data.repository.RepositoryException;
import io.basc.framework.data.repository.UpdateOperation;
import io.basc.framework.lang.Nullable;
import io.basc.framework.orm.EntityOperations;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.SqlOperations;
import io.basc.framework.sql.template.dialect.SqlDialect;
import io.basc.framework.util.Elements;

public interface SqlTemplate extends EntityOperations, SqlOperations {
	default long count(Sql sql) {
		Sql countSql = getMapper().toCountSql(sql);
		return query(long.class, countSql).getElements().first();
	}

	default void createTable(Class<?> entityClass) {
		createTable(entityClass, null);
	}

	default void createTable(Class<?> entityClass, @Nullable String tableName) {
		TableMapping<?> tableMapping = getMapper().getMapping(entityClass);
		createTable(tableMapping, tableName);
	}

	default void createTable(TableMapping<?> tableMapping, String tableName) {
		Elements<Sql> sqls = getMapper().toCreateTableSql(tableMapping, tableName);
		for (Sql sql : sqls) {
			execute(sql);
		}
	}

	@Override
	default long delete(DeleteOperation operation) throws RepositoryException {
		Sql sql = getMapper().toSql(operation);
		return update(sql);
	}

	SqlDialect getMapper();

	@Override
	default long insert(InsertOperation operation) throws RepositoryException {
		Sql sql = getMapper().toSql(operation);
		return update(sql);
	}

	@Override
	default <T> Query<T> query(TypeDescriptor resultTypeDescriptor, QueryOperation operation)
			throws RepositoryException {
		Sql sql = getMapper().toSql(operation);
		return query(resultTypeDescriptor, sql);
	}

	@Override
	default long update(UpdateOperation operation) throws RepositoryException {
		Sql sql = getMapper().toSql(operation);
		return update(sql);
	}
}
