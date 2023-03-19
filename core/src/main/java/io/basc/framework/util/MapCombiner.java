package io.basc.framework.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MapCombiner<K, V> implements Selector<Map<K, V>> {

	@Override
	public Map<K, V> apply(List<Map<K, V>> elements) {
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
