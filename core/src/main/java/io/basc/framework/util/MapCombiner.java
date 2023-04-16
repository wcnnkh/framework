package io.basc.framework.util;

import java.util.Collections;
import java.util.Map;

public class MapCombiner<K, V> implements Selector<Map<K, V>> {
	private static final MapCombiner<?, ?> SINGLETON = new MapCombiner<>();

	@Override
	public Map<K, V> apply(Elements<Map<K, V>> elements) {
		Map<K, V> target = null;
		for (Map<K, V> map : elements) {
			if (map == null || map.isEmpty()) {
				continue;
			}

			if (target == null) {
				target = CollectionFactory.createApproximateMap(map, 16);
			}

			target.putAll(map);
		}
		return target == null ? Collections.emptyMap() : target;
	}

	@SuppressWarnings("unchecked")
	public static <L, R> MapCombiner<L, R> getSingleton() {
		return (MapCombiner<L, R>) SINGLETON;
	}
}
