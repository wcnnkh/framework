package io.basc.framework.orm.repository;

import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.orm.EntityOperations;
import io.basc.framework.orm.OrmException;
import io.basc.framework.util.ResultSet;
import io.basc.framework.util.page.Paginations;

public interface CurdOperations extends EntityOperations {

	<T> long deleteAll(Class<? extends T> entityClass, T conditions);

	<T> long deleteAll(Class<? extends T> entityClass);

	default <T> T getById(Class<? extends T> entityClass, Object... primaryKeys) {
		return getById(TypeDescriptor.valueOf(entityClass), entityClass, primaryKeys);
	};

	<T, E> T getById(TypeDescriptor resultsTypeDescriptor, Class<? extends E> entityClass, Object... primaryKeys)
			throws OrmException;

	default <K, T> ResultSet<T> getInIds(Class<? extends T> entityClass, List<? extends K> inPrimaryKeys,
			Object... primaryKeys) throws OrmException {
		return getInIds(TypeDescriptor.valueOf(entityClass), entityClass, inPrimaryKeys, primaryKeys);
	}

	<K, T> ResultSet<T> getInIds(TypeDescriptor resultsTypeDescriptor, Class<?> entityClass,
			List<? extends K> inPrimaryKeys, Object... primaryKeys) throws OrmException;

	default <T> Paginations<T> query(Class<? extends T> entityClass, T conditions) throws OrmException {
		return query(TypeDescriptor.valueOf(entityClass), entityClass, conditions);
	}

	<T, E> Paginations<T> query(TypeDescriptor resultsTypeDescriptor, Class<? extends E> entityClass, E conditions)
			throws OrmException;

	default <T> Paginations<T> queryAll(Class<? extends T> entityClass) throws OrmException {
		return queryAll(TypeDescriptor.valueOf(entityClass), entityClass);
	}

	<T, E> Paginations<T> queryAll(TypeDescriptor resultsTypeDescriptor, Class<? extends E> entityClass)
			throws OrmException;

	<T> long updateAll(Class<? extends T> entityClass, T entity, T conditions);
}
