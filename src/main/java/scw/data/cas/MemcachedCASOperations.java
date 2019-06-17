package scw.data.cas;

import java.util.Collection;
import java.util.Map;

import scw.data.memcached.Memcached;

public final class MemcachedCASOperations implements CASOperations {
	private final Memcached memcached;

	public MemcachedCASOperations(Memcached memcached) {
		this.memcached = memcached;
	}

	public boolean cas(String key, Object value, int exp, long cas) {
		return memcached.cas(key, exp, value, cas);
	}

	public boolean delete(String key, long cas) {
		return memcached.delete(key, cas, Long.MAX_VALUE);
	}

	public <T> CAS<T> get(String key) {
		return memcached.gets(key);
	}

	public boolean set(String key, Object value, int exp) {
		return memcached.set(key, exp, value);
	}

	public boolean delete(String key) {
		return memcached.delete(key);
	}

	public boolean add(String key, Object value, int exp) {
		return memcached.add(key, exp, value);
	}

	public <T> Map<String, CAS<T>> gets(Collection<String> keys) {
		return memcached.gets(keys);
	}

}
