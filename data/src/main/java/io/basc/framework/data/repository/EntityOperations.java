package io.basc.framework.data.repository;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.domain.Query;

public interface EntityOperations {
	<T> boolean insert(Class<? extends T> entityClass, T entity);

	<T> boolean delete(Class<? extends T> entityClass, T entity);

	boolean deleteAll(Class<?> entityClass);

	<T> boolean update(Class<? extends T> entityClass, T entity);

	<T, R> Query<R> select(TypeDescriptor resultTypeDescriptor, Class<? extends T> entityClass, T entity);

	<R> Query<R> selectAll(TypeDescriptor resultTypeDescriptor, Class<?> entityClass);
}
