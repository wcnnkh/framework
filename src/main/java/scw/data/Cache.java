package scw.data;

import java.util.Collection;
import java.util.Map;

public interface Cache {
	<T> T get(String key);
	
	<T> T getAndTouch(String key);

	void set(String key, Object value);
	
	boolean add(String key, Object value);
	
	boolean touch(String key);

	boolean delete(String key);

	boolean isExist(String key);
	
	<T> Map<String, T> get(Collection<String> keyCollections);
}
