package scw.core.lazy;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
public abstract class UnsafeMapLazyFactory<K, V> extends AbstractMapLazyFactory<K, V> {
	private Map<K, V> map;

	public int size() {
		return map == null ? 0 : map.size();
	}

	public boolean isEmpty() {
		return map == null ? true : map.isEmpty();
	}

	public boolean containsKey(Object key) {
		return map == null ? false : map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map == null ? false : map.containsValue(value);
	}

	public V get(Object key) {
		V v;
		K k = (K) key;
		if (map == null) {
			map = createMap();
			v = createValue(k);
			map.put(k, v);
		} else {
			v = map.get(k);
		}
		return v;
	}

	public V put(K key, V value) {
		if (map == null) {
			map = createMap();
		}

		return map.put(key, value);
	}

	public V remove(Object key) {
		if (map == null) {
			return null;
		}

		return map.remove(key);
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		if (map == null) {
			map = createMap();
		}

		map.putAll(m);
	}

	public void clear() {
		if (map != null) {
			map.clear();
		}
	}

	public Set<K> keySet() {
		return map == null ? Collections.EMPTY_SET : map.keySet();
	}

	public Collection<V> values() {
		return map == null ? Collections.EMPTY_LIST : map.values();
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map == null ? Collections.EMPTY_SET : map.entrySet();
	}
}
