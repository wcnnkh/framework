package shuchaowen.core.db.storage.cache;

import shuchaowen.core.cache.Memcached;

public class MemcachedCache implements Cache{
	private final Memcached memcached;
	
	public MemcachedCache(Memcached memcached){
		this.memcached = memcached;
	}

	public <T> T getAndTouch(Class<T> type, String key, int exp) {
		return memcached.getAndTocuh(key, exp);
	}

	public void set(String key, int exp, Object data) {
		memcached.add(key, exp, data);
	}

	public void add(String key, int exp, Object data) {
		memcached.set(key, exp, data);
	}

	public void delete(String ...key) {
		for(String k : key){
			memcached.delete(k);
		}
	}
}
