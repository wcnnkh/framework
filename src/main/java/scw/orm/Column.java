package scw.orm;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import scw.core.reflect.FieldDefinition;

public interface Column extends FieldDefinition {
	Method getGetter();

	Method getSetter();

	Class<?> getType();

	Type getGenericType();
	
	Object get(Object obj) throws ORMException;

	void set(Object obj, Object value) throws ORMException;
}
