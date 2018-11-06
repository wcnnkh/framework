package shuchaowen.memcached;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 使用的CAS乐观锁 单个map请不要保存过多的数据，因为在取数据时会先把所有数据取到内存中来 推荐用于存储单用户的数据，而不是所有用户的公用数据
 * 
 * @author shuchaowen
 *
 */
public class MemcachedMap<V> implements Map<String, V> {
	private Memcached memcached;
	private String mapKey;

	public MemcachedMap(Memcached memcached, String mapKey) {
		this.memcached = memcached;
		this.mapKey = mapKey;
	}

	public int size() {
		Map<String, V> map = memcached.get(mapKey);
		return map == null ? 0 : map.size();
	}

	public boolean isEmpty() {
		Map<String, V> map = memcached.get(mapKey);
		return map == null ? true : map.isEmpty();
	}

	public boolean containsKey(Object key) {
		Map<String, V> map = memcached.get(mapKey);
		return map == null ? false : map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		Map<String, V> map = memcached.get(mapKey);
		return map == null ? false : map.containsValue(value);
	}

	public V get(Object key) {
		Map<String, V> map = memcached.get(mapKey);
		return map == null ? null : map.get(key);
	}

	public V put(String key, V value) {
		boolean b = false;
		V v = null;
		while (!b) {
			CAS<Map<String, V>> casMap = memcached.gets(mapKey);
			if (casMap == null || casMap.getValue() == null) {
				Map<String, V> map = new HashMap<String, V>();
				v = map.put(key, value);
				b = memcached.add(mapKey, map);
			} else {
				v = casMap.getValue().put(key, value);
				b = memcached.cas(mapKey, casMap.getValue(), casMap.getCas());
			}
		}
		return v;
	}

	public V remove(Object key) {
		boolean b = false;
		V v = null;
		while (!b) {
			CAS<Map<String, V>> casMap = memcached.gets(mapKey);
			if (casMap == null || casMap.getValue() == null) {
				HashMap<String, V> map = new HashMap<String, V>();
				b = memcached.add(mapKey, map);
			} else {
				v = casMap.getValue().remove(mapKey);
				b = memcached.cas(mapKey, casMap.getValue(), casMap.getCas());
			}
		}
		return v;
	}

	public void putAll(Map<? extends String, ? extends V> m) {
		boolean b = false;
		while (!b) {
			CAS<Map<String, V>> casMap = memcached.gets(mapKey);
			if (casMap == null || casMap.getValue() == null) {
				b = memcached.add(mapKey, m);
			} else {
				casMap.getValue().putAll(m);
				b = memcached.cas(mapKey, casMap.getValue(), casMap.getCas());
			}
		}
	}

	public void clear() {
		boolean b = false;
		while (!b) {
			CAS<Map<String, V>> casMap = memcached.gets(mapKey);
			if (casMap == null || casMap.getValue() == null) {
				b = memcached.delete(mapKey);
			} else {
				casMap.getValue().clear();
				b = memcached.cas(mapKey, casMap.getValue(), casMap.getCas());
			}
		}
	}

	public Set<String> keySet() {
		Map<String, V> map = memcached.get(mapKey);
		return map == null ? new HashMap<String, V>(0).keySet() : map.keySet();
	}

	public Collection<V> values() {
		Map<String, V> map = memcached.get(mapKey);
		return map == null ? new HashMap<String, V>(0).values() : map.values();
	}

	public Set<java.util.Map.Entry<String, V>> entrySet() {
		Map<String, V> map = memcached.get(mapKey);
		return map == null ? new HashMap<String, V>(0).entrySet() : map.entrySet();
	}

}
