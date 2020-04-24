package scw.data.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import scw.core.utils.CollectionUtils;
import scw.data.cas.CAS;
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

	public boolean remove(String key) {
		CAS<LinkedHashMap<String, V>> cas = getCasMap();
		if (cas == null) {
			return false;
		}

		LinkedHashMap<String, V> valueMap = cas.getValue();
		boolean b = valueMap.containsKey(key);
		valueMap.remove(key);
		if (!memcached.getCASOperations().cas(dataKey, valueMap, 0, cas.getCas())) {
			return remove(key);
		}

		if (b) {
			memcached.decr(sizeKey, 1);
		}
		return b;
	}

	public boolean containsKey(String key) {
		Map<String, V> map = getMap();
		if (map == null) {
			return false;
		}

		return map.containsKey(key);
	}

	public void put(String key, V value) {
		CAS<LinkedHashMap<String, V>> cas = getCasMap();
		if (cas == null) {
			LinkedHashMap<String, V> valueMap = new LinkedHashMap<String, V>();
			valueMap.put(key, value);
			if (!memcached.getCASOperations().cas(dataKey, valueMap, 0, 0)) {
				put(key, value);
				return;
			}

			memcached.incr(sizeKey, 1, 1);
		} else {
			LinkedHashMap<String, V> valueMap = cas.getValue();
			boolean b = valueMap.containsKey(key);
			valueMap.put(key, value);
			if (!memcached.getCASOperations().cas(dataKey, valueMap, 0, cas.getCas())) {
				put(key, value);
				return;
			}

			if (b) {
				memcached.incr(sizeKey, 1);
			}
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

	public boolean putIfAbsent(String key, V value) {
		CAS<LinkedHashMap<String, V>> cas = getCasMap();
		if (cas == null) {
			LinkedHashMap<String, V> valueMap = new LinkedHashMap<String, V>();
			valueMap.put(key, value);
			if (!memcached.getCASOperations().cas(dataKey, valueMap, 0, 0)) {
				return putIfAbsent(key, value);
			}

			memcached.incr(sizeKey, 1, 1);
			return true;
		} else {
			LinkedHashMap<String, V> valueMap = cas.getValue();
			boolean b = valueMap.containsKey(key);
			valueMap.putIfAbsent(key, value);
			if (!memcached.getCASOperations().cas(dataKey, valueMap, 0, cas.getCas())) {
				return putIfAbsent(key, value);
			}
			if (b) {
				memcached.incr(sizeKey, 1);
			}
			return b;
		}
	}

	public void putAll(Map<? extends String, ? extends V> m) {
		if (CollectionUtils.isEmpty(m)) {
			return;
		}

		CAS<LinkedHashMap<String, V>> cas = getCasMap();
		if (cas == null) {
			LinkedHashMap<String, V> valueMap = new LinkedHashMap<String, V>();
			valueMap.putAll(m);
			if (!memcached.getCASOperations().cas(dataKey, valueMap, 0, 0)) {
				putAll(m);
				return;
			}

			memcached.incr(sizeKey, m.size(), m.size());
		} else {
			LinkedHashMap<String, V> valueMap = cas.getValue();
			int oldSize = valueMap.size();
			valueMap.putAll(m);
			int size = valueMap.size() - oldSize;
			if (!memcached.getCASOperations().cas(dataKey, valueMap, 0, cas.getCas())) {
				putAll(m);
				return;
			}
			memcached.incr(sizeKey, size, size);
		}
	}

	public Map<String, V> asMap() {
		return getMap();
	}

	@SuppressWarnings("unchecked")
	private CAS<LinkedHashMap<String, V>> getCasMap() {
		CAS<Object> v = memcached.getCASOperations().get(dataKey);
		return v == null ? null
				: new CAS<LinkedHashMap<String, V>>(v.getCas(), (LinkedHashMap<String, V>) v.getValue());
	}

	@SuppressWarnings("unchecked")
	private LinkedHashMap<String, V> getMap() {
		return (LinkedHashMap<String, V>) memcached.get(dataKey);
	}

	public V getAndRemove(String key) {
		CAS<LinkedHashMap<String, V>> cas = getCasMap();
		if (cas == null) {
			return null;
		}

		LinkedHashMap<String, V> valueMap = cas.getValue();
		boolean b = valueMap.containsKey(key);
		V v = valueMap.remove(key);
		if (!memcached.getCASOperations().cas(dataKey, valueMap, 0, cas.getCas())) {
			return getAndRemove(key);
		}

		if (b) {
			memcached.decr(sizeKey, 1);
		}
		return v;
	}

	public V getAndPut(String key, V value) {
		CAS<LinkedHashMap<String, V>> cas = getCasMap();
		if (cas == null) {
			LinkedHashMap<String, V> valueMap = new LinkedHashMap<String, V>();
			V v = valueMap.put(key, value);
			if (!memcached.getCASOperations().cas(dataKey, valueMap, 0, 0)) {
				return getAndPut(key, value);
			}

			memcached.incr(sizeKey, 1, 1);
			return v;
		} else {
			LinkedHashMap<String, V> valueMap = cas.getValue();
			boolean b = valueMap.containsKey(key);
			V v = valueMap.put(key, value);
			if (!memcached.getCASOperations().cas(dataKey, valueMap, 0, cas.getCas())) {
				return getAndPut(key, value);
			}

			if (b) {
				memcached.incr(sizeKey, 1);
			}
			return v;
		}
	}

	public V getAndPutIfAbsent(String key, V value) {
		CAS<LinkedHashMap<String, V>> cas = getCasMap();
		if (cas == null) {
			LinkedHashMap<String, V> valueMap = new LinkedHashMap<String, V>();
			V v = valueMap.put(key, value);
			if (!memcached.getCASOperations().cas(dataKey, valueMap, 0, 0)) {
				return getAndPutIfAbsent(key, value);
			}

			memcached.incr(sizeKey, 1, 1);
			return v;
		} else {
			LinkedHashMap<String, V> valueMap = cas.getValue();
			boolean b = valueMap.containsKey(key);
			V v = valueMap.putIfAbsent(key, value);
			if (!memcached.getCASOperations().cas(dataKey, valueMap, 0, cas.getCas())) {
				return getAndPutIfAbsent(key, value);
			}
			if (b) {
				memcached.incr(sizeKey, 1);
			}
			return v;
		}
	}
}
