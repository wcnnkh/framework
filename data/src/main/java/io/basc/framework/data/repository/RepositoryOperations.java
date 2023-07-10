package io.basc.framework.data.repository;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.Query;

/**
 * 存储库的操作
 * 
 * @author wcnnkh
 *
 */
public interface RepositoryOperations {
	/**
	 * 执行
	 * 
	 * @param operation
	 * @return
	 * @throws RepositoryException
	 */
	long execute(Operation operation) throws RepositoryException;

	/**
	 * 删除
	 * 
	 * @param operation
	 * @return
	 */
	default long delete(DeleteOperation operation) throws RepositoryException {
		return execute(operation);
	}

	/**
	 * 插入
	 * 
	 * @param operation
	 * @return
	 */
	default long insert(InsertOperation operation) throws RepositoryException {
		return execute(operation);
	}

	/**
	 * 查询
	 * 
	 * @param <T>
	 * @param resultTypeDescriptor
	 * @param select
	 * @return
	 */
	<T> Query<T> query(TypeDescriptor resultTypeDescriptor, QueryOperation operation) throws RepositoryException;

	/**
	 * 更新
	 * 
	 * @param operation
	 * @return
	 */
	default long update(UpdateOperation operation) throws RepositoryException {
		return execute(operation);
	}
}
