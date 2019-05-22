package scw.db.cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CacheManager {
	void save(Object bean);

	void update(Object bean);

	void delete(Object bean);

	void deleteById(Class<?> type, Object... params);

	void saveOrUpdate(Object bean);

	<T> T getById(Class<T> type, Object... params);

	<T> List<T> getByIdList(Class<T> type, Object... params);

	<K, V> Map<K, V> getInIdList(Class<V> type, Collection<K> inIds,
			Object... params);
	
	boolean isExist(Class<?> type, Object... params);
}
