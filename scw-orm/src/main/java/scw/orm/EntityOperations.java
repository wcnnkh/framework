package scw.orm;

public interface EntityOperations {
	void save(Object entity);

	void delete(Object entity);

	void deleteById(Class<?> entityClass, Object... ids);

	void update(Object entity);

	<T> T getById(Class<? extends T> entityClass, Object... ids);

	boolean exists(Class<?> entityClass, Object... ids);
}
