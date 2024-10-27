package io.basc.framework.observe.properties;

import io.basc.framework.convert.lang.ObjectValue;

@FunctionalInterface
public interface PropertyWrapper {
	static final PropertyWrapper CREATOR = new PropertyWrapper() {

		public ObjectValue wrap(String key, Object value) {
			return ObjectValue.of(value);
		}
	};

	ObjectValue wrap(String key, Object value);
}
