package scw.core.reflect;

import java.lang.reflect.Field;

import scw.core.AnnotationFactory;

public interface FieldDefinition extends AnnotationFactory{
	Field getField();

	Object get(Object obj) throws Exception;

	void set(Object obj, Object value) throws Exception;

	Class<?> getType();

	String getName();

	int getModifiers();
	
	boolean isStatic();
	
	boolean isTransient();
}
