package io.basc.framework.data.repository;

import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.page.Pagination;

/**
 * 增删改查
 * 
 * @author wcnnkh
 *
 */
public interface Repository {
	/**
	 * 增(保存)
	 * 
	 * @param sourceTypeDescriptor
	 * @param columns
	 * @return
	 */
	boolean save(TypeDescriptor sourceTypeDescriptor, List<RepositoryColumn> columns);

	/**
	 * 删
	 * 
	 * @param sourceTypeDescriptor
	 * @param conditions
	 * @return
	 */
	boolean delete(TypeDescriptor sourceTypeDescriptor, Conditions conditions);

	/**
	 * 修改
	 * 
	 * @param sourceTypeDescriptor
	 * @param columns
	 * @param conditions
	 * @return
	 */
	boolean update(TypeDescriptor sourceTypeDescriptor, List<RepositoryColumn> columns, Conditions conditions);

	/**
	 * 查询
	 * 
	 * @param targetTypeDescriptor
	 * @param conditions
	 * @param orders
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	<T> Pagination<T> select(TypeDescriptor targetTypeDescriptor, Conditions conditions, List<OrderColumn> orders,
			long pageNum, int pageSize);
}
