package scw.data.cache;

/**
 * 临时缓存
 * @author shuchaowen
 *
 */
public interface TemporaryCache {
	Object get(String key);

	Object getAndTouch(String key, int exp);

	void touch(String key, int exp);

	void delete(String key);

	void set(String key, int exp, Object value);
}
