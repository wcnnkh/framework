package scw.orm;

import scw.core.reflect.FieldDefinition;

public interface SetterFilterChain {
	void setter(FieldDefinition fieldDefinition, Object bean, Object value)
			throws Throwable;
}
