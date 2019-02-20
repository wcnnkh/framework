package scw.sql.orm.cache;

import java.util.Collection;
import java.util.Map;

public interface Cache {
	
	<T> T get(Class<T> type, String key);
	
	void delete(String key);

	void add(String key, Object bean);

	void set(String key, Object bean);
	
	<T> Map<String, T> getMap(Class<T> type, Collection<String> keys);
}
