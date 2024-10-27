package io.basc.framework.transform.factory.config;

import io.basc.framework.convert.lang.ObjectValue;
import io.basc.framework.transform.factory.PropertyFactory;

public interface EditablePropertyFactory extends PropertyFactory {
	ObjectValue put(String key, ObjectValue value);

	ObjectValue put(String key, Object value);

	ObjectValue remove(String key);
}
