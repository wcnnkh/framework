package scw.data;

import java.util.Collection;
import java.util.Map;

/**
 * 存储管理
 * @author shuchaowen
 *
 */

public interface Storage {
	<T> T get(String key);

	<T> Map<String, T> get(Collection<String> keys);

	/**
	 * 如果不存在就添加
	 * @param key
	 * @param value
	 * @return
	 */
	boolean add(String key, Object value);

	void set(String key, Object value);

	boolean isExist(String key);
	
	boolean delete(String key);

	void delete(Collection<String> keys);
}
