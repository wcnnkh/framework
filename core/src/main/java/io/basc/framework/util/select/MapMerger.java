package io.basc.framework.util.select;

import java.util.Collections;
import java.util.Map;

import io.basc.framework.util.CollectionFactory;
import io.basc.framework.util.Elements;

public class MapMerger<K, V> implements Merger<Map<K, V>> {
	private static final MapMerger<?, ?> SINGLETON = new MapMerger<>();

	@SuppressWarnings("unchecked")
	public static <L, R> MapMerger<L, R> getSingleton() {
		return (MapMerger<L, R>) SINGLETON;
	}

	@Override
	public Map<K, V> merge(Elements<? extends Map<K, V>> elements) {
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
