package io.basc.framework.observe.properties;

import io.basc.framework.convert.lang.ValueWrapper;

@FunctionalInterface
public interface PropertyWrapper {
	static final PropertyWrapper CREATOR = new PropertyWrapper() {

		public ValueWrapper wrap(String key, Object value) {
			return ValueWrapper.of(value);
		}
	};

	ValueWrapper wrap(String key, Object value);
}
