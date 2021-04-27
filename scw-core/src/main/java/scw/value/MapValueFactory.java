package scw.value;

import java.util.Map;

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
