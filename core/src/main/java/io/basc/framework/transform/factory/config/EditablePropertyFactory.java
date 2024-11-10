package io.basc.framework.transform.factory.config;

import io.basc.framework.convert.lang.ValueWrapper;
import io.basc.framework.transform.factory.PropertyFactory;

public interface EditablePropertyFactory extends PropertyFactory {
	ValueWrapper put(String key, ValueWrapper value);

	ValueWrapper put(String key, Object value);

	ValueWrapper remove(String key);
}
