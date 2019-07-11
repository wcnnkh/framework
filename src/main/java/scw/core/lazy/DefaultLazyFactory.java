package scw.core.lazy;

import java.util.HashMap;
import java.util.Map;

public abstract class DefaultLazyFactory<K, V> implements LazyFactory<K, V> {
	private Map<K, V> map;

	protected Map<K, V> initMap() {
		return new HashMap<K, V>();
	}

	public V get(K key) {
		V v;
		if (map == null) {
			map = initMap();
			v = createValue(key);
			map.put(key, v);
		} else {
			v = map.get(key);
			if (v == null) {
				v = createValue(key);
				map.put(key, v);
			}
		}
		return v;
	}

	protected abstract V createValue(K key);
}
