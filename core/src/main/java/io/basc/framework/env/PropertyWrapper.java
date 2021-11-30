package io.basc.framework.env;

import io.basc.framework.value.AnyValue;
import io.basc.framework.value.StringValue;
import io.basc.framework.value.Value;

@FunctionalInterface
public interface PropertyWrapper {
	static final PropertyWrapper CREATOR = new PropertyWrapper() {

		public Value wrap(String key, Object value) {
			if (value instanceof Value) {
				return (Value) value;
			} else if (value instanceof String) {
				return new StringValue((String) value);
			} else {
				return new AnyValue(value);
			}
		}
	};

	Value wrap(String key, Object value);
}
