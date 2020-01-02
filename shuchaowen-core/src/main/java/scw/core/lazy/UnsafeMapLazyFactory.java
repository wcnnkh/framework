package scw.core.lazy;

import java.util.Map;

@SuppressWarnings("unchecked")
public abstract class UnsafeMapLazyFactory<K, V> extends
		AbstractMapLazyFactory<K, V> {
	private Map<K, V> map;

	@Override
	protected Map<K, V> getMap(boolean init) {
		if (init && map == null) {
			map = createMap();
		}
		return map;
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
			if (isCreateNewValue(k, v)) {
				v = createValue(k);
				map.put(k, v);
			}
		}
		return v;
	}

	public V put(K key, V value) {
		return getMap(true).put(key, value);
	}

	public V remove(Object key) {
		if (map == null) {
			return null;
		}

		return map.remove(key);
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		getMap(true).putAll(m);
	}

	public void clear() {
		if (map != null) {
			map.clear();
		}
	}
}
