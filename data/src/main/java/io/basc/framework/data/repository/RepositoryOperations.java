package io.basc.framework.data.repository;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.data.domain.Query;

/**
 * 存储库的操作
 * 
 * @author wcnnkh
 *
 */
public interface RepositoryOperations {

	/**
	 * 删除
	 * 
	 * @param operation
	 * @return
	 */
	long delete(DeleteOperation operation) throws RepositoryException;

	/**
	 * 插入
	 * 
	 * @param operation
	 * @return
	 */
	long insert(InsertOperation operation) throws RepositoryException;

	/**
	 * 查询
	 * 
	 * @param <T>
	 * @param resultType
	 * @param operation
	 * @return
	 * @throws RepositoryException
	 */
	default <T> Query<T> query(Class<T> resultType, QueryOperation operation) throws RepositoryException {
		return query(TypeDescriptor.valueOf(resultType), operation);
	}

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

	/**
	 * 更新
	 * 
	 * @param operation
	 * @return
	 */
	long update(UpdateOperation operation) throws RepositoryException;
}
