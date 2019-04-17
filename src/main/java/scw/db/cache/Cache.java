package scw.db.cache;

import java.util.Collection;
import java.util.Map;

public interface Cache {
	void add(String key, Object value, int exp);

	void set(String key, Object value, int exp);

	void delete(String key);

	<T> T get(Class<T> type, String key);

	<T> T getAndTouch(Class<T> type, String key, int exp);

	<T> Map<String, T> get(Class<T> type, Collection<String> keys);

	Map<String, String> getMap(String key);

	void mapAdd(String key, String field, String value);

	void mapRemove(String key, String field);
}
