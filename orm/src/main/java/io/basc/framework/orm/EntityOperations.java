package io.basc.framework.orm;

import io.basc.framework.aop.support.ProxyUtils;
import io.basc.framework.lang.Nullable;

public interface EntityOperations {
	default Class<?> getUserClass(Class<?> entityClass) {
		return ProxyUtils.getFactory().getUserClass(entityClass);
	}
	
	default boolean save(Object entity) {
		if (entity == null) {
			return false;
		}

		return save(getUserClass(entity.getClass()), entity);
	}

	<T> boolean save(Class<? extends T> entityClass, T entity);

	default boolean delete(Object entity) {
		if (entity == null) {
			return false;
		}

		return delete(getUserClass(entity.getClass()), entity);
	}

	<T> boolean delete(Class<? extends T> entityClass, T entity);

	boolean deleteById(Class<?> entityClass, Object... ids);

	default boolean update(Object entity) {
		if (entity == null) {
			return false;
		}

		return update(getUserClass(entity.getClass()), entity);
	}

	<T> boolean update(Class<? extends T> entityClass, T entity);

	default boolean saveOrUpdate(Object entity) {
		if (entity == null) {
			return false;
		}

		return saveOrUpdate(getUserClass(entity.getClass()), entity);
	}

	<T> boolean saveOrUpdate(Class<? extends T> entityClass, T entity);

	@Nullable
	<T> T getById(Class<? extends T> entityClass, Object... ids);
}
