package io.basc.framework.transform.factory.config;

import io.basc.framework.convert.lang.Value;
import io.basc.framework.transform.factory.PropertyFactory;

public interface EditablePropertyFactory extends PropertyFactory {
	Value put(String key, Value value);

	Value put(String key, Object value);

	Value remove(String key);
}
