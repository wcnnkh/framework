package io.basc.framework.data.repository;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.Query;

/**
 * 对存储库的操作
 * 
 * @author shuchaowen
 *
 */
public interface RepositoryOperations {
	/**
	 * 插入操作
	 * 
	 * @param operation
	 * @return
	 */
	boolean insert(InsertOperation operation);

	/**
	 * 删除操作
	 * 
	 * @param operation
	 * @return
	 */
	boolean delete(DeleteOperation operation);

	/**
	 * 更新操作
	 * 
	 * @param operation
	 * @return
	 */
	boolean update(UpdateOperation operation);

	/**
	 * 查询操作
	 * 
	 * @param <T>
	 * @param resultTypeDescriptor
	 * @param select
	 * @return
	 */
	<T> Query<T> select(TypeDescriptor resultTypeDescriptor, SelectOperation operation);

}
