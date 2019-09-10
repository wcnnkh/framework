package scw.data.cache;

import scw.data.memcached.Memcached;

public final class MemcachedTemporaryCache implements TemporaryCache {
	private Memcached memcached;

	public MemcachedTemporaryCache(Memcached memcached) {
		this.memcached = memcached;
	}

	public Object get(String key) {
		return memcached.get(key);
	}

	public Object getAndTouch(String key, int exp) {
		return memcached.getAndTouch(key, exp);
	}

	public void touch(String key, int exp) {
		memcached.touch(key, exp);
	}

	public void delete(String key) {
		memcached.delete(key);
	}

	public void set(String key, int exp, Object value) {
		memcached.set(key, exp, value);
	}

}
