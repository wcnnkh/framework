package scw.data.cache;

import scw.beans.annotation.AutoImpl;

/**
 * 临时缓存
 * 
 * @author shuchaowen
 *
 */
@AutoImpl(impl = { MemcachedTemporaryCache.class, RedisTemporaryCache.class,
		MemoryTemporaryCache.class })
public interface TemporaryCache {
	Object get(String key);

	Object getAndTouch(String key, int exp);

	void touch(String key, int exp);

	void delete(String key);

	void set(String key, int exp, Object value);
}
