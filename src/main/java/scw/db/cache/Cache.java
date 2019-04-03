package scw.db.cache;

import java.util.Collection;
import java.util.Map;

public interface Cache {
	void add(String key, Object value, CacheConfig config);

	void set(String key, Object value, CacheConfig config);

	void delete(String key);

	<T> T get(Class<T> type, String key);

	<T> T getAndTouch(Class<T> type, String key, CacheConfig config);

	<T> Map<String, T> get(Class<T> type, Collection<String> keys);

	Map<String, String> getMap(String key);

	void mapAdd(String key, String field, String value);

	void mapRemove(String key, String field);
}
