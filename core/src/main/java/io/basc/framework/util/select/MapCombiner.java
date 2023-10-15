package io.basc.framework.util.select;

import java.util.Collections;
import java.util.Map;

import io.basc.framework.util.CollectionFactory;
import io.basc.framework.util.element.Elements;

public class MapCombiner<K, V> implements Selector<Map<K, V>> {
	private static final MapCombiner<?, ?> SINGLETON = new MapCombiner<>();

	@SuppressWarnings("unchecked")
	public static <L, R> MapCombiner<L, R> getSingleton() {
		return (MapCombiner<L, R>) SINGLETON;
	}

	@Override
	public Map<K, V> apply(Elements<? extends Map<K, V>> elements) {
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
}