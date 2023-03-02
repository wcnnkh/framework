package io.basc.framework.orm;

import io.basc.framework.aop.support.ProxyUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;

public interface EntityOperations {
	<T> boolean delete(Class<? extends T> entityClass, T entity);

	default <T> boolean delete(T entity) {
		if (entity == null) {
			return false;
		}

		return delete(getUserClass(entity.getClass()), entity);
	}

	<T> boolean deleteById(Class<? extends T> entityClass, Object... ids);

	@Nullable
	<T> T getById(Class<? extends T> entityClass, Object... ids);

	default Class<?> getUserClass(Class<?> entityClass) {
		return ProxyUtils.getFactory().getUserClass(entityClass);
	}

	<T> boolean isPresent(Class<? extends T> entityClass, T entity);

	<T> boolean isPresentById(Class<? extends T> entityClass, Object... ids);

	<T> void save(Class<? extends T> entityClass, T entity);

	default <T> void save(T entity) {
		Assert.requiredArgument(entity != null, "entity");
		save(getUserClass(entity.getClass()), entity);
	}

	default <T> boolean saveIfAbsent(Class<? extends T> entityClass, T entity) {
		// 这种实现线程不安全，如果能实现原子性的实现更好
		if (isPresent(entityClass, entity)) {
			return false;
		}
		save(entityClass, entity);
		return true;
	}

	default boolean saveIfAbsent(Object entity) {
		Assert.requiredArgument(entity != null, "entity");
		return saveIfAbsent(getUserClass(entity.getClass()), entity);
	}

	default <T> boolean saveOrUpdate(Class<? extends T> entityClass, T entity) {
		if (saveIfAbsent(entityClass, entity)) {
			return true;
		}
		return update(entityClass, entity);
	}

	default <T> boolean saveOrUpdate(T entity) {
		Assert.requiredArgument(entity != null, "entity");
		return saveOrUpdate(getUserClass(entity.getClass()), entity);
	}

	<T> boolean update(Class<? extends T> entityClass, T entity);

	default <T> boolean update(T entity) {
		if (entity == null) {
			return false;
		}

		return update(getUserClass(entity.getClass()), entity);
	}
}
