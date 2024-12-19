package io.basc.framework.observe.properties;

import io.basc.framework.core.convert.Value;

@FunctionalInterface
public interface PropertyWrapper {
	static final PropertyWrapper CREATOR = new PropertyWrapper() {

		public Value wrap(String key, Object value) {
			return Value.of(value);
		}
	};

	Value wrap(String key, Object value);
}
