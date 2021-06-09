package scw.orm;

import scw.lang.Nullable;

public interface EntityOperations {
	boolean save(Object entity);

	boolean delete(Object entity);

	boolean deleteById(Class<?> entityClass, Object... ids);

	boolean update(Object entity);

	boolean saveOrUpdate(Object entity);

	@Nullable
	<T> T getById(Class<? extends T> entityClass, Object... ids);
}
