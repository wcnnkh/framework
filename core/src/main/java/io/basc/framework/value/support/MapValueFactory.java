package io.basc.framework.value.support;

import java.util.Map;

import io.basc.framework.value.Value;
import io.basc.framework.value.ValueFactory;

public class MapValueFactory<K> implements ValueFactory<K> {
	protected final Map<K, ?> map;

	public MapValueFactory(Map<K, ?> map) {
		this.map = map;
	}

	public Value get(K key) {
		if (map == null) {
			return null;
		}

		Object value = map.get(key);
		return Value.of(value);
	}
}
