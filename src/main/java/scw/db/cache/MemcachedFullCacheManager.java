package scw.db.cache;

import java.util.Collection;
import java.util.Map;

import scw.data.cas.CasUtils;
import scw.data.memcached.Memcached;

public final class MemcachedFullCacheManager extends FullCacheManager {
	private final Memcached memcached;

	public MemcachedFullCacheManager(Memcached memcached) {
		this.memcached = memcached;
	}

	public void add(String key, Object value) {
		memcached.add(key, value);
	}

	public void set(String key, Object value) {
		memcached.set(key, value);
	}

	public void delete(String key) {
		memcached.delete(key);
	}

	public <T> T get(Class<T> type, String key) {
		return memcached.get(key);
	}

	public <T> Map<String, T> get(Class<T> type, Collection<String> keys) {
		return memcached.get(keys);
	}

	public Map<String, String> getMap(String key) {
		return memcached.get(key);
	}

	public void mapAdd(String key, String field, String value) {
		CasUtils.mapPut(memcached.getCASOperations(), key, field, value);
	}

	public void mapRemove(String key, String field) {
		CasUtils.mapRemove(memcached.getCASOperations(), key, field);
	}
}
