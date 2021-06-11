package scw.orm;

import scw.aop.support.ProxyUtils;
import scw.lang.Nullable;

public interface EntityOperations {
	default boolean save(Object entity) {
		return save(ProxyUtils.getFactory().getUserClass(entity.getClass()), entity);
	}

	<T> boolean save(Class<? extends T> entityClass, T entity);

	default boolean delete(Object entity) {
		return delete(ProxyUtils.getFactory().getUserClass(entity.getClass()), entity);
	}

	<T> boolean delete(Class<? extends T> entityClass, T entity);

	boolean deleteById(Class<?> entityClass, Object... ids);

	default boolean update(Object entity) {
		return update(ProxyUtils.getFactory().getUserClass(entity.getClass()), entity);
	}

	<T> boolean update(Class<? extends T> entityClass, T entity);

	default boolean saveOrUpdate(Object entity) {
		return saveOrUpdate(ProxyUtils.getFactory().getUserClass(entity.getClass()), entity);
	}

	<T> boolean saveOrUpdate(Class<? extends T> entityClass, T entity);

	@Nullable
	<T> T getById(Class<? extends T> entityClass, Object... ids);
}
