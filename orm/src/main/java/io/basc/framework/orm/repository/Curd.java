package io.basc.framework.orm.repository;

import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.orm.OrmException;
import io.basc.framework.util.page.Paginations;

/**
 * curd操作
 * 
 * @see CurdRepository
 * @author wcnnkh
 *
 * @param <V>
 */
public interface Curd<V> {
	boolean delete(V entity);

	long deleteAll();

	long deleteAll(V conditions);

	boolean deleteById(Object... ids);

	V getById(Object... ids);

	<T> T getById(TypeDescriptor resultsTypeDescriptor, Object... entityIds) throws OrmException;

	List<V> getInIds(List<?> inIds, Object... ids);

	<T> List<T> getInIds(TypeDescriptor resultsTypeDescriptor, List<?> entityInIds, Object... entityIds)
			throws OrmException;

	boolean isPresentById(Object... ids);

	boolean isPresent(V conditions);

	<T> Paginations<T> query(TypeDescriptor resultsTypeDescriptor, V conditions) throws OrmException;

	Paginations<V> query(V conditions);

	Paginations<V> queryAll();

	<T> Paginations<T> queryAll(TypeDescriptor resultsTypeDescriptor) throws OrmException;

	void save(V entity);

	boolean saveIfAbsent(V entity);

	boolean update(V entity);

	long updateAll(V entity, V conditions);
}
