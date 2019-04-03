package scw.db.cache;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.memcached.CAS;
import scw.memcached.Memcached;
import scw.sql.orm.ORMUtils;

public final class MemcachedCache implements Cache {
	private final Memcached memcached;

	public MemcachedCache(Memcached memcached) {
		this.memcached = memcached;
	}

	public void add(String key, Object value, CacheConfig config) {
		memcached.add(key, (int) config.timeUnit().toSeconds(config.exp()), value);
	}

	public void set(String key, Object value, CacheConfig config) {
		memcached.set(key, (int) config.timeUnit().toSeconds(config.exp()), value);
	}

	public void delete(String key) {
		memcached.delete(key);
	}

	public <T> T get(Class<T> type, String key) {
		T t = memcached.get(key);
		return ORMUtils.restartFieldLinsten(t);
	}

	public <T> T getAndTouch(Class<T> type, String key, CacheConfig config) {
		T t = memcached.getAndTocuh(key, (int) config.timeUnit().toSeconds(config.exp()));
		return ORMUtils.restartFieldLinsten(t);
	}

	public <T> Map<String, T> get(Class<T> type, Collection<String> keys) {
		Map<String, T> map = memcached.get(keys);
		if (map == null) {
			return null;
		}

		for (Entry<String, T> entry : map.entrySet()) {
			map.put(entry.getKey(), ORMUtils.restartFieldLinsten(entry.getValue()));
		}
		return map;
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
