package io.basc.framework.mapper;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.value.Value;

public interface Getter extends FieldDescriptor {
	public static final String BOOLEAN_GETTER_METHOD_PREFIX = "is";
	public static final String DEFAULT_GETTER_METHOD_PREFIX = "get";

	Object get(Object instance);

	@Nullable
	default Value getValue(Object instance) {
		Object value = get(instance);
		if (value == null) {
			return null;
		}

		if (value instanceof Value) {
			return (Value) value;
		}

		return Value.of(value, new TypeDescriptor(this));
	}

	default Parameter getParameter(Object instance) {
		return new Parameter(getName(), getValue(instance));
	}
}
