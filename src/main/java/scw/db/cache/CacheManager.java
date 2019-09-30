package scw.db.cache;

public interface CacheManager {
	void save(Object bean);

	void update(Object bean);

	void delete(Object bean);

	void deleteById(Class<?> type, Object... params);

	void saveOrUpdate(Object bean);

	<T> T getById(Class<T> type, Object... params);
	
	boolean isExistById(Class<?> type, Object ...params);
}
