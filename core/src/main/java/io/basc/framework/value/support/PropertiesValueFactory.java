package io.basc.framework.value.support;

import java.util.Properties;

import io.basc.framework.value.Value;
import io.basc.framework.value.ValueFactory;

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
