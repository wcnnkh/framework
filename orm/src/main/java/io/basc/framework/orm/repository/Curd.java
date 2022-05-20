package io.basc.framework.orm.repository;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.PageRequest;
import io.basc.framework.orm.OrmException;
import io.basc.framework.util.page.Paginations;
import io.basc.framework.util.stream.Cursor;

import java.util.List;
import java.util.stream.Collectors;

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

	<T> T getById(TypeDescriptor resultsTypeDescriptor, Object... entityIds)
			throws OrmException;

	List<V> getInIds(List<?> inIds, Object... ids);

	<T> List<T> getInIds(TypeDescriptor resultsTypeDescriptor,
			List<?> entityInIds, Object... entityIds) throws OrmException;

	boolean isPresentById(Object... ids);

	boolean isPresent(V conditions);

	boolean isPresentAny(V conditions);

	<T> Paginations<T> pagingQuery(TypeDescriptor resultsTypeDescriptor,
			V conditions, PageRequest request) throws OrmException;

	Paginations<V> pagingQuery(V conditions, PageRequest request);

	<T> Cursor<T> query(TypeDescriptor resultsTypeDescriptor, V conditions,
			PageRequest request) throws OrmException;

	Cursor<V> query(V conditions, PageRequest request);

	Cursor<V> queryAll();

	<T> Cursor<T> queryAll(TypeDescriptor resultsTypeDescriptor)
			throws OrmException;

	<T> Cursor<T> queryAll(TypeDescriptor resultsTypeDescriptor, V conditions)
			throws OrmException;

	Cursor<V> queryAll(V conditions);

	@SuppressWarnings("unchecked")
	default <T> List<T> queryList(TypeDescriptor resultsTypeDescriptor,
			V conditions) throws OrmException {
		return (List<T>) queryAll(resultsTypeDescriptor, conditions).collect(
				Collectors.toList());
	}

	List<V> queryList(V conditions);

	void save(V entity);

	boolean saveIfAbsent(V entity);

	boolean update(V entity);

	long updateAll(V entity, V conditions);
}
