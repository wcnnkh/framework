package scw.db.cache;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.data.memcached.CAS;
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
		while (casMapAdd(key, field, value)) {
			break;
		}
	}

	@SuppressWarnings("unchecked")
	private boolean casMapAdd(String key, String field, String value) {
		CAS<Object> cas = memcached.gets(key);
		if (cas == null) {
			LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
			valueMap.put(field, value);
			return memcached.cas(key, valueMap, 0);
		} else {
			LinkedHashMap<String, String> valueMap = (LinkedHashMap<String, String>) cas
					.getValue();
			valueMap.put(field, value);
			return memcached.cas(key, valueMap, cas.getCas());
		}
	}

	public void mapRemove(String key, String field) {
		while (casMapRemove(key, field)) {
			break;
		}
	}

	@SuppressWarnings("unchecked")
	private boolean casMapRemove(String key, String field) {
		CAS<Object> cas = memcached.gets(key);
		if (cas == null) {
			return true;
		}

		LinkedHashMap<String, String> valueMap = (LinkedHashMap<String, String>) cas
				.getValue();
		valueMap.remove(field);
		return memcached.cas(key, valueMap, cas.getCas());
	}

}
