package io.basc.framework.value.support;

import io.basc.framework.value.AnyValue;
import io.basc.framework.value.Value;
import io.basc.framework.value.ValueFactory;

import java.util.Properties;

public class PropertiesValueFactory<K> implements ValueFactory<K> {
	protected final Properties properties;

	public PropertiesValueFactory(Properties properties) {
		this.properties = properties;
	}

	public Value getValue(K key) {
		if (properties == null) {
			return null;
		}

		Object value = properties.get(key);
		return value == null ? null : createValue(value);
	}

	protected Value createValue(Object value) {
		return new AnyValue(value);
	}
}
