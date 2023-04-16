package io.basc.framework.value;

import java.util.Properties;

public class PropertiesValueFactory<K> implements ValueFactory<K> {
	protected final Properties properties;

	public PropertiesValueFactory(Properties properties) {
		this.properties = properties;
	}

	public Value get(K key) {
		if (properties == null) {
			return null;
		}

		Object value = properties.get(key);
		return Value.of(value);
	}
}
