package run.soeasy.framework.core.collection.function;

import java.util.Collections;
import java.util.Map;

import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.Elements;

public class MapMerger<K, V> implements Merger<Map<K, V>> {
	static final MapMerger<?, ?> INSTANCE = new MapMerger<>();

	@Override
	public Map<K, V> select(Elements<Map<K, V>> elements) {
		Map<K, V> target = null;
		for (Map<K, V> map : elements) {
			if (map == null || map.isEmpty()) {
				continue;
			}

			if (target == null) {
				target = CollectionUtils.createApproximateMap(map, 16);
			}

			target.putAll(map);
		}
		return target == null ? Collections.emptyMap() : target;
	}

}