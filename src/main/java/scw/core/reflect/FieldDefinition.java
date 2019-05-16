package scw.core.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface FieldDefinition {
	Field getField();

	<T extends Annotation> T getAnnotation(Class<T> type);

	Object get(Object obj) throws Exception;

	void set(Object obj, Object value) throws Exception;

	Class<?> getType();

	String getName();

	int getModifiers();
	
	boolean isStatic();
	
	boolean isTransient();
}
