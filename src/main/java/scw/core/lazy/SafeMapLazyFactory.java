package scw.core.lazy;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
public abstract class SafeMapLazyFactory<K, V> extends AbstractMapLazyFactory<K, V> {
	private volatile Map<K, V> map;

	public int size() {
		return map == null ? 0 : map.size();
	}

	public boolean isEmpty() {
		return map == null ? false : map.isEmpty();
	}

	public boolean containsKey(Object key) {
		return map == null ? false : map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map == null ? false : map.containsValue(value);
	}

	private void initCreateMap() {
		if (map == null) {
			synchronized (this) {
				if (map == null) {
					map = createMap();
				}
			}
		}
	}

	public V get(Object key) {
		V v = null;
		K k = (K) key;
		initCreateMap();
		v = map.get(k);
		if (v == null) {
			synchronized (map) {
				if (v == null) {
					v = createValue(k);
					map.put(k, v);
				}
			}
		}
		return v;
	}

	public V put(K key, V value) {
		initCreateMap();
		synchronized (map) {
			return map.put(key, value);
		}
	}

	public V remove(Object key) {
		if (map == null) {
			synchronized (this) {
				if (map == null) {
					return null;
				}
			}
		}

		synchronized (map) {
			return map.remove(key);
		}
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		initCreateMap();
		synchronized (map) {
			map.putAll(m);
		}
	}

	public void clear() {
		if (map == null) {
			synchronized (this) {
				if (map == null) {
					return;
				}
			}
		}

		synchronized (map) {
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
