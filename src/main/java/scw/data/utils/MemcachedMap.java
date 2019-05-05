package scw.data.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import scw.data.memcached.CAS;
import scw.data.memcached.Memcached;

public final class MemcachedMap<V> implements scw.data.utils.Map<String, V> {
	private final Memcached memcached;
	private final String dataKey;
	private final String sizeKey;

	public MemcachedMap(Memcached memcached, String key) {
		this.memcached = memcached;
		this.dataKey = key;
		this.sizeKey = dataKey + "#size";
	}

	public V get(String key) {
		Map<String, V> map = getMap();
		if (map == null) {
			return null;
		}

		return map.get(key);
	}

	public V remove(String key) {
		CAS<LinkedHashMap<String, V>> cas = getCasMap();
		if (cas == null) {
			return null;
		}

		LinkedHashMap<String, V> valueMap = cas.getValue();
		V v = valueMap.remove(key);
		if (!memcached.cas(dataKey, valueMap, cas.getCas())) {
			v = remove(key);
		} else {
			memcached.set(sizeKey, valueMap.size());
		}
		return v;
	}

	public boolean containsKey(String key) {
		Map<String, V> map = getMap();
		if (map == null) {
			return false;
		}

		return map.containsKey(key);
	}

	public V put(String key, V value) {
		CAS<LinkedHashMap<String, V>> cas = getCasMap();
		if (cas == null) {
			LinkedHashMap<String, V> valueMap = new LinkedHashMap<String, V>();
			V v = valueMap.put(key, value);
			if (!memcached.cas(dataKey, valueMap, 0)) {
				v = put(key, value);
			} else {
				memcached.set(sizeKey, 1);
			}
			return v;
		} else {
			LinkedHashMap<String, V> valueMap = cas.getValue();
			V v = valueMap.put(key, value);
			if (!memcached.cas(dataKey, valueMap, cas.getCas())) {
				v = put(key, value);
			} else {
				memcached.set(sizeKey, valueMap.size());
			}
			return v;
		}
	}

	public int size() {
		return (int) longSize();
	}

	public long longSize() {
		Long size = (Long) memcached.get(sizeKey);
		return size == null ? 0 : size;
	}

	public boolean isEmpty() {
		return longSize() == 0;
	}

	public V putIfAbsent(String key, V value) {
		CAS<LinkedHashMap<String, V>> cas = getCasMap();
		if (cas == null) {
			LinkedHashMap<String, V> valueMap = new LinkedHashMap<String, V>();
			V v = valueMap.putIfAbsent(key, value);
			if (!memcached.cas(dataKey, valueMap, 0)) {
				v = put(key, value);
			} else {
				memcached.set(sizeKey, 1);
			}
			return v;
		} else {
			LinkedHashMap<String, V> valueMap = cas.getValue();
			V v = valueMap.putIfAbsent(key, value);
			if (!memcached.cas(dataKey, valueMap, cas.getCas())) {
				v = put(key, value);
			} else {
				memcached.set(sizeKey, valueMap.size());
			}
			return v;
		}
	}

	public void putAll(Map<? extends String, ? extends V> m) {
		CAS<LinkedHashMap<String, V>> cas = getCasMap();
		if (cas == null) {
			LinkedHashMap<String, V> valueMap = new LinkedHashMap<String, V>();
			valueMap.putAll(m);
			if (!memcached.cas(dataKey, valueMap, 0)) {
				putAll(m);
			} else {
				memcached.set(sizeKey, valueMap.size());
			}
		} else {
			LinkedHashMap<String, V> valueMap = cas.getValue();
			valueMap.putAll(m);
			if (!memcached.cas(dataKey, valueMap, cas.getCas())) {
				putAll(m);
			} else {
				memcached.set(sizeKey, valueMap.size());
			}
		}
	}

	public Map<String, V> asLocalMap() {
		return getMap();
	}

	@SuppressWarnings("unchecked")
	private CAS<LinkedHashMap<String, V>> getCasMap() {
		CAS<Object> v = memcached.gets(dataKey);
		return v == null ? null : new CAS<LinkedHashMap<String, V>>(v.getCas(),
				(LinkedHashMap<String, V>) v.getValue());
	}

	@SuppressWarnings("unchecked")
	private LinkedHashMap<String, V> getMap() {
		return (LinkedHashMap<String, V>) memcached.get(dataKey);
	}
}
