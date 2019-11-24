package scw.orm;

import scw.core.reflect.FieldDefinition;

public interface GetterFilter {
	Object getter(FieldDefinition fieldDefinition, Object bean, GetterFilterChain chain) throws Throwable;
}