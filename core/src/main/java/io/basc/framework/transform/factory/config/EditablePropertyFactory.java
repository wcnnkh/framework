package io.basc.framework.transform.factory.config;

import io.basc.framework.core.convert.Any;
import io.basc.framework.transform.factory.PropertyFactory;

public interface EditablePropertyFactory extends PropertyFactory {
	Any put(String key, Any value);

	Any put(String key, Object value);

	Any remove(String key);
}
