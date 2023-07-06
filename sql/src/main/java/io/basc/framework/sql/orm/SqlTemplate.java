package io.basc.framework.sql.orm;

import java.util.List;
import java.util.OptionalLong;
import java.util.stream.Collectors;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.Query;
import io.basc.framework.data.repository.Operation;
import io.basc.framework.data.repository.QueryOperation;
import io.basc.framework.data.repository.RepositoryException;
import io.basc.framework.lang.Nullable;
import io.basc.framework.orm.EntityOperations;
import io.basc.framework.sql.Sql;
import io.basc.framework.sql.SqlOperations;
import io.basc.framework.util.Elements;

public interface SqlTemplate extends EntityOperations, SqlOperations {
	SqlDialect getMapper();

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
	default List<OptionalLong> batchExecute(List<? extends Operation> operations) throws RepositoryException {
		List<Sql> sqls = operations.stream().map((e) -> getMapper().toSql(e)).collect(Collectors.toList());
		return executeBatch(sqls);
	}

	@Override
	default <T> Query<T> query(TypeDescriptor resultTypeDescriptor, QueryOperation operation)
			throws RepositoryException {
		Sql sql = getMapper().toSql(operation);
		return query(resultTypeDescriptor, sql);
	}
}
