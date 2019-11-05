package scw.data;

import java.util.Collection;
import java.util.Map;

/**
 * 简单缓存
 * @author shuchaowen
 *
 */
public interface SimpleCacheService {
	<T> T get(String key);

	<T> T getAndTouch(String key, int newExp);

	void set(String key, Object value);

	void set(String key, int exp, Object value);

	boolean add(String key, Object value);

	boolean add(String key, int exp, Object value);

	boolean touch(String key, int exp);

	boolean delete(String key);

	boolean isExist(String key);

	<T> Map<String, T> get(Collection<String> keyCollections);
}
