package scw.orm;

import java.lang.reflect.Type;

import scw.core.reflect.AnnotationFactory;

public interface Column extends AnnotationFactory {
	Class<?> getType();

	Type getGenericType();

	Class<?> getDeclaringClass();

	String getName();

	Object get(Object obj) throws ORMException;

	void set(Object obj, Object value) throws ORMException;
}
