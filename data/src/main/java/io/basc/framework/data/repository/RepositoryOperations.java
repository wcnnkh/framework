package io.basc.framework.data.repository;

import java.util.OptionalLong;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.Query;
import io.basc.framework.util.Elements;

/**
 * 对存储库的操作
 * 
 * @author wcnnkh
 *
 */
public interface RepositoryOperations {
	/**
	 * 执行操作
	 * 
	 * @param operation
	 * @return
	 * @throws RepositoryException
	 */
	default OptionalLong execute(Operation operation) throws RepositoryException {
		return batchExecute(Elements.singleton(operation)).first();
	}

	/**
	 * 批量执行操作
	 * 
	 * @param operations
	 * @return
	 * @throws RepositoryException
	 */
	Elements<OptionalLong> batchExecute(Elements<? extends Operation> operations) throws RepositoryException;

	/**
	 * 删除操作
	 * 
	 * @param operation
	 * @return
	 */
	default OptionalLong delete(DeleteOperation operation) throws RepositoryException {
		return batchDelete(Elements.forArray(operation)).first();
	}

	default Elements<OptionalLong> batchDelete(Elements<? extends DeleteOperation> operations)
			throws RepositoryException {
		return batchExecute(operations);
	}

	/**
	 * 保存操作
	 * 
	 * @param operation
	 * @return
	 */
	default OptionalLong insert(InsertOperation operation) throws RepositoryException {
		return batchInsert(Elements.forArray(operation)).first();
	}

	default Elements<OptionalLong> batchInsert(Elements<? extends InsertOperation> operations)
			throws RepositoryException {
		return batchExecute(operations);
	}

	/**
	 * 查询操作
	 * 
	 * @param <T>
	 * @param operation
	 * @param resultTypeDescriptor
	 * @return
	 * @throws RepositoryException
	 */
	<T> Query<T> query(QueryOperation operation, TypeDescriptor resultTypeDescriptor) throws RepositoryException;

	/**
	 * 更新操作
	 * 
	 * @param operation
	 * @return
	 */
	default OptionalLong update(UpdateOperation operation) throws RepositoryException {
		return batchUpdate(Elements.singleton(operation)).first();
	}

	default Elements<OptionalLong> batchUpdate(Elements<? extends UpdateOperation> operations) {
		return batchExecute(operations);
	}
}
