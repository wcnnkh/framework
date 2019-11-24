package scw.orm;

import scw.core.reflect.FieldDefinition;

public interface SetterFilter {
	void setter(FieldDefinition fieldDefinition, Object bean, Object value,
			SetterFilterChain chain) throws Throwable;
}