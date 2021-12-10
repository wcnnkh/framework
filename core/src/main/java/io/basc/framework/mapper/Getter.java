package io.basc.framework.mapper;

public interface Getter extends FieldDescriptor {
	public static final String BOOLEAN_GETTER_METHOD_PREFIX = "is";
	public static final String DEFAULT_GETTER_METHOD_PREFIX = "get";

	Object get(Object instance);
}
