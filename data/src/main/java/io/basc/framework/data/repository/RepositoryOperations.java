package io.basc.framework.data.repository;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalLong;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.Query;

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
		return batchExecute(Arrays.asList(operation)).get(0);
	}

	/**
	 * 批量执行操作
	 * 
	 * @param operations
	 * @return
	 * @throws RepositoryException
	 */
	List<OptionalLong> batchExecute(List<? extends Operation> operations) throws RepositoryException;

	/**
	 * 删除操作
	 * 
	 * @param operation
	 * @return
	 */
	default OptionalLong delete(DeleteOperation operation) throws RepositoryException {
		return batchDelete(Arrays.asList(operation)).get(0);
	}

	default List<OptionalLong> batchDelete(List<? extends DeleteOperation> operations) throws RepositoryException {
		return batchExecute(operations);
	}

	/**
	 * 保存操作
	 * 
	 * @param operation
	 * @return
	 */
	default OptionalLong insert(InsertOperation operation) throws RepositoryException {
		return batchInsert(Arrays.asList(operation)).get(0);
	}

	default List<OptionalLong> batchInsert(List<? extends InsertOperation> operations) throws RepositoryException {
		return batchExecute(operations);
	}

	/**
	 * 查询操作
	 * 
	 * @param <T>
	 * @param resultTypeDescriptor
	 * @param select
	 * @return
	 */
	<T> Query<T> query(TypeDescriptor resultTypeDescriptor, QueryOperation operation) throws RepositoryException;

	/**
	 * 更新操作
	 * 
	 * @param operation
	 * @return
	 */
	default OptionalLong update(UpdateOperation operation) throws RepositoryException {
		return batchUpdate(Arrays.asList(operation)).get(0);
	}

	default List<OptionalLong> batchUpdate(List<? extends UpdateOperation> operations) {
		return batchExecute(operations);
	}
}
