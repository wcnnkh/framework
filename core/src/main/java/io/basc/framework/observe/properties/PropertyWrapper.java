package io.basc.framework.observe.properties;

import io.basc.framework.core.convert.Any;

@FunctionalInterface
public interface PropertyWrapper {
	static final PropertyWrapper CREATOR = new PropertyWrapper() {

		public Any wrap(String key, Object value) {
			return Any.of(value);
		}
	};

	Any wrap(String key, Object value);
}
