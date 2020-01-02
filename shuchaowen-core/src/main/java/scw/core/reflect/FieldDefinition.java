package scw.core.reflect;

import java.lang.reflect.Field;

public interface FieldDefinition extends AnnotationFactory {
	Field getField();

	Object get(Object obj) throws Exception;

	void set(Object obj, Object value) throws Exception;

	Class<?> getDeclaringClass();

	String getName();
}
