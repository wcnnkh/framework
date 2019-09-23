package scw.data.cache;

import scw.beans.annotation.AutoImpl;

/**
 * 临时缓存
 * 
 * @author shuchaowen
 *
 */
@AutoImpl({ MemcachedTemporaryCache.class, RedisTemporaryCache.class,
		MemoryTemporaryCache.class })
public interface TemporaryCache {
	<T> T get(String key);

	<T> T getAndTouch(String key, int exp);

	void touch(String key, int exp);

	void delete(String key);

	void set(String key, int exp, Object value);
}
