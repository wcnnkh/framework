package io.basc.framework.mapper;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.Value;

public interface Getter extends FieldDescriptor {
	public static final String BOOLEAN_GETTER_METHOD_PREFIX = "is";
	public static final String DEFAULT_GETTER_METHOD_PREFIX = "get";

	Object get(Object instance);

	default Value getValue(Object instance) {
		return new AnyValue(get(instance), new TypeDescriptor(this));
	}

	default Parameter getParameter(Object instance) {
		return new Parameter(getName(), getValue(instance));
	}
}
