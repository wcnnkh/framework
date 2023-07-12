package io.basc.framework.data.repository;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.Query;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Elements;

/**
 * 存储库的操作
 * 
 * @author wcnnkh
 *
 */
public interface RepositoryOperations {
	default long insert(InsertOperationSymbol insertOperationSymbol, Elements<? extends Expression> columns,
			Repository repository) {
		Operation operation = new Operation(insertOperationSymbol, columns, repository);
		return execute(operation);
	}

	default long delete(DeleteOperationSymbol deleteOperationSymbol, Repository repository,
			@Nullable Elements<? extends Condition> conditions) {
		Operation operation = new Operation(deleteOperationSymbol, repository, conditions);
		return execute(operation);
	}

	default long update(Repository repository, Elements<? extends Expression> columns,
			@Nullable Elements<? extends Condition> conditions) {
		return update(UpdateOperationSymbol.UPDATE, repository, columns, conditions);
	}

	default long insert(Elements<? extends Expression> columns, Repository repository) {
		return insert(InsertOperationSymbol.INSERT, columns, repository);
	}

	default long delete(Repository repository, @Nullable Elements<? extends Condition> conditions) {
		return delete(DeleteOperationSymbol.DELETE, repository, conditions);
	}

	default long update(UpdateOperationSymbol updateOperationSymbol, Repository repository,
			Elements<? extends Expression> columns, @Nullable Elements<? extends Condition> conditions) {
		Operation operation = new Operation(updateOperationSymbol, repository, columns, conditions);
		return execute(operation);
	}

	/**
	 * 执行
	 * 
	 * @param operation
	 * @return
	 * @throws RepositoryException
	 */
	long execute(Operation operation) throws RepositoryException;

	/**
	 * 查询
	 * 
	 * @param <T>
	 * @param resultTypeDescriptor
	 * @param operation
	 * @return
	 * @throws RepositoryException
	 */
	<T> Query<T> query(TypeDescriptor resultTypeDescriptor, QueryOperation operation) throws RepositoryException;
}
