package scw.core.lazy;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMapLazyFactory<K, V> implements MapLazyFactory<K, V> {

	public Map<K, V> createMap() {
		return new HashMap<K, V>();
	}
}
