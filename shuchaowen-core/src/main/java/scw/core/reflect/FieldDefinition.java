package scw.core.reflect;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

public interface FieldDefinition {
	AnnotatedElement getAnnotatedElement();
	
	Field getField();

	Object get(Object obj) throws Exception;

	void set(Object obj, Object value) throws Exception;

	Class<?> getDeclaringClass();

	String getName();
}
