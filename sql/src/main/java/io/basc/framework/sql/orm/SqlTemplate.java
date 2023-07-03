package io.basc.framework.sql.orm;

import java.util.List;
import java.util.OptionalLong;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.Query;
import io.basc.framework.data.repository.Operation;
import io.basc.framework.data.repository.QueryOperation;
import io.basc.framework.data.repository.RepositoryException;
import io.basc.framework.data.repository.UpdateOperationSymbol;
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
	default Elements<OptionalLong> batchExecute(Elements<? extends Operation> operations) throws RepositoryException {
		// TODO 假实现，后续需要修改
		List<OptionalLong> results = operations.map((operation) -> {
			Sql sql = getMapper().toOperationSql(operation);
			if (operation.getOperationSymbol().getName().equals(UpdateOperationSymbol.UPDATE.getName())) {
				int update = update(sql);
				return OptionalLong.of(update);
			} else {
				boolean executed = execute(sql);
				return OptionalLong.of(executed ? 1 : 0);
			}
		}).toList();
		return Elements.of(results);
	}

	@Override
	default <T> Query<T> query(QueryOperation operation, TypeDescriptor resultTypeDescriptor)
			throws RepositoryException {
		Sql sql = getMapper().toOperationSql(operation);
		return query(resultTypeDescriptor, sql);
	}
}
