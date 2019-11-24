package scw.orm;

import scw.core.reflect.FieldDefinition;

public interface GetterFilterChain {
	Object getter(FieldDefinition fieldDefinition, Object bean)
			throws Throwable;
}
