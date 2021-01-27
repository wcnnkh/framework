package scw.value.factory.support;

import java.util.Map;

import scw.value.AnyValue;
import scw.value.Value;
import scw.value.factory.ValueFactory;

public class MapValueFactory<K> implements ValueFactory<K> {
	protected final Map<K, ?> map;

	public MapValueFactory(Map<K, ?> map) {
		this.map = map;
	}

	public Value getValue(K key) {
		if (map == null) {
			return null;
		}

		Object value = map.get(key);
		return value == null ? null : createValue(value);
	}

	protected Value createValue(Object value) {
		return new AnyValue(value);
	}
}
