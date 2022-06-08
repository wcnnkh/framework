package io.basc.framework.orm.repository;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.PageRequest;
import io.basc.framework.orm.EntityOperations;
import io.basc.framework.orm.OrmException;
import io.basc.framework.util.page.Paginations;
import io.basc.framework.util.stream.Cursor;

import java.util.List;
import java.util.stream.Collectors;

public interface CurdRepository extends EntityOperations {

	<T> long deleteAll(Class<? extends T> entityClass, T conditions);

	<T> long deleteAll(Class<? extends T> entityClass);

	default <T> T getById(java.lang.Class<? extends T> entityClass,
			Object... ids) {
		return getById(TypeDescriptor.valueOf(entityClass), entityClass, ids);
	};

	<T, E> T getById(TypeDescriptor resultsTypeDescriptor,
			Class<? extends E> entityClass, Object... entityIds)
			throws OrmException;

	default <T> List<T> getInIds(Class<? extends T> entityClass,
			List<?> entityInIds, Object... entityIds) throws OrmException {
		return getInIds(TypeDescriptor.valueOf(entityClass), entityClass,
				entityInIds, entityIds);
	}

	<T, E> List<T> getInIds(TypeDescriptor resultsTypeDescriptor,
			Class<? extends E> entityClass, List<?> entityInIds,
			Object... entityIds) throws OrmException;

	<T> boolean isPresentAny(Class<? extends T> entityClass, T conditions);

	default <T> Paginations<T> pagingQuery(Class<? extends T> entityClass,
			T conditions, PageRequest request) throws OrmException {
		return pagingQuery(TypeDescriptor.valueOf(entityClass), entityClass,
				conditions, request);
	}

	<T, E> Paginations<T> pagingQuery(TypeDescriptor resultsTypeDescriptor,
			Class<? extends E> entityClass, E conditions, PageRequest request)
			throws OrmException;

	default <T> Cursor<T> query(Class<? extends T> entityClass, T conditions,
			PageRequest request) throws OrmException {
		return query(TypeDescriptor.valueOf(entityClass), entityClass,
				conditions, request);
	}

	<T, E> Cursor<T> query(TypeDescriptor resultsTypeDescriptor,
			Class<? extends E> entityClass, E conditions, PageRequest request)
			throws OrmException;

	default <T> Cursor<T> queryAll(Class<? extends T> entityClass, T conditions)
			throws OrmException {
		return queryAll(TypeDescriptor.valueOf(entityClass), entityClass,
				conditions);
	}

	<T, E> Cursor<T> queryAll(TypeDescriptor resultsTypeDescriptor,
			Class<? extends E> entityClass, E conditions) throws OrmException;

	default <T> Cursor<T> queryAll(Class<? extends T> entityClass)
			throws OrmException {
		return queryAll(TypeDescriptor.valueOf(entityClass), entityClass);
	}

	<T, E> Cursor<T> queryAll(TypeDescriptor resultsTypeDescriptor,
			Class<? extends E> entityClass) throws OrmException;

	default <T> List<T> queryList(Class<? extends T> entityClass, T conditions)
			throws OrmException {
		return queryList(TypeDescriptor.valueOf(entityClass), entityClass,
				conditions);
	}

	@SuppressWarnings("unchecked")
	default <T, E> List<T> queryList(TypeDescriptor resultsTypeDescriptor,
			Class<? extends E> entityClass, E conditions) throws OrmException {
		return (List<T>) queryAll(resultsTypeDescriptor, entityClass,
				conditions).collect(Collectors.toList());
	}

	<T> long updateAll(Class<? extends T> entityClass, T entity, T conditions);
}
