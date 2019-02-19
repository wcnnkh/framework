package scw.sql.orm.cache;

import scw.memcached.Memcached;

public class MemcachedCache implements Cache {
	private final Memcached memcached;
	private final int exp;

	public MemcachedCache(Memcached memcached, int exp) {
		this.memcached = memcached;
		this.exp = exp;
	}

	public <T> T get(Class<T> type, String key) {
		return memcached.getAndTocuh(key, exp);
	}

	public void delete(String key) {
		memcached.delete(key);
	}

	public void add(String key, Object data) {
		memcached.add(key, exp, data);
	}

	public void set(String key, Object data) {
		memcached.set(key, exp, data);
	}

}
