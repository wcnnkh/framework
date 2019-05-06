package scw.data.cache;

import java.util.Collection;
import java.util.Map;

public interface Cache {

	boolean save(String key, Object bean, int exp);

	boolean update(String key, Object bean, int exp);

	void saveOrUpdate(String key, Object bean, int exp);

	boolean delete(String key);

	<T> T getAndTouch(Class<T> type, String key, int exp);

	boolean containsKey(String key);

	<T> Map<String, T> gets(Class<T> type, Collection<String> keys);
}
