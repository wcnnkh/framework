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
		byte[] data = (byte[]) memcached.get(key);
		if (data == null) {
			return null;
		}

		return CacheUtils.decode(type, data);
	}

	public <T> T getAndTouch(Class<T> type, String key, int exp) {
		byte[] data = (byte[]) memcached.getAndTouch(key, exp);
		if (data == null) {
			return null;
		}

		return CacheUtils.decode(type, data);
	}

	public <T> Map<String, T> get(Class<T> type, Collection<String> keys) {
		Map<String, Object> map = memcached.get(keys);
		if (map == null) {
			return null;
		}

		Map<String, T> valueMap = new HashMap<String, T>();
		for (Entry<String, Object> entry : map.entrySet()) {
			byte[] data = (byte[]) entry.getValue();
			if (data == null) {
				continue;
			}

			valueMap.put(entry.getKey(), CacheUtils.decode(type, data));
		}
		return valueMap;
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getMap(String key) {
		return (Map<String, String>) memcached.get(key);
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
