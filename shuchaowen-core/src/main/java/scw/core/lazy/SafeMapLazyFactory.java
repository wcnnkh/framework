package scw.core.lazy;

import java.util.Map;

@SuppressWarnings("unchecked")
public abstract class SafeMapLazyFactory<K, V> extends
		AbstractMapLazyFactory<K, V> {
	private volatile Map<K, V> map;

	@Override
	protected Map<K, V> getMap(boolean init) {
		if (init) {
			if (map == null) {
				synchronized (this) {
					if (map == null) {
						map = createMap();
					}
				}
			}
		}
		return map;
	}

	public V get(Object key) {
		V v = null;
		K k = (K) key;
		getMap(true);
		v = map.get(k);
		if (isCreateNewValue(k, v)) {
			synchronized (map) {
				v = map.get(k);
				if (isCreateNewValue(k, v)) {
					v = createValue(k);
					map.put(k, v);
				}
			}
		}
		return v;
	}

	public V put(K key, V value) {
		getMap(true);
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
		getMap(true);
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
}
