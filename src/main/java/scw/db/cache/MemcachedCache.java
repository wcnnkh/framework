package scw.db.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.data.memcached.CAS;
import scw.data.memcached.Memcached;

public final class MemcachedCache implements Cache {
	private final Memcached memcached;

	public MemcachedCache(Memcached memcached) {
		this.memcached = memcached;
	}

	public void add(String key, Object value, int exp) {
		memcached.add(key, exp, CacheUtils.encode(value));
	}

	public void set(String key, Object value, int exp) {
		memcached.set(key, exp, CacheUtils.encode(value));
	}

	public void delete(String key) {
		memcached.delete(key);
	}

	public <T> T get(Class<T> type, String key) {
		byte[] data = memcached.get(key);
		if (data == null) {
			return null;
		}

		return CacheUtils.decode(type, data);
	}

	public <T> T getAndTouch(Class<T> type, String key, int exp) {
		byte[] data = memcached.getAndTouch(key, exp);
		if (data == null) {
			return null;
		}

		return CacheUtils.decode(type, data);
	}

	public <T> Map<String, T> get(Class<T> type, Collection<String> keys) {
		Map<String, byte[]> map = memcached.get(keys);
		if (map == null) {
			return null;
		}

		Map<String, T> valueMap = new HashMap<String, T>();
		for (Entry<String, byte[]> entry : map.entrySet()) {
			byte[] data = entry.getValue();
			if (data == null) {
				continue;
			}

			valueMap.put(entry.getKey(), CacheUtils.decode(type, data));
		}
		return valueMap;
	}

	public Map<String, String> getMap(String key) {
		return memcached.get(key);
	}

	public void mapAdd(String key, String field, String value) {
		while (casMapAdd(key, field, value)) {
			break;
		}
	}

	private boolean casMapAdd(String key, String field, String value) {
		CAS<LinkedHashMap<String, String>> cas = memcached.gets(key);
		if (cas == null) {
			LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
			valueMap.put(field, value);
			return memcached.cas(key, valueMap, 0);
		} else {
			LinkedHashMap<String, String> valueMap = cas.getValue();
			valueMap.put(field, value);
			return memcached.cas(key, valueMap, cas.getCas());
		}
	}

	public void mapRemove(String key, String field) {
		while (casMapRemove(key, field)) {
			break;
		}
	}

	private boolean casMapRemove(String key, String field) {
		CAS<LinkedHashMap<String, String>> cas = memcached.gets(key);
		if (cas == null) {
			return true;
		}

		LinkedHashMap<String, String> valueMap = cas.getValue();
		valueMap.remove(field);
		return memcached.cas(key, valueMap, cas.getCas());
	}

}
