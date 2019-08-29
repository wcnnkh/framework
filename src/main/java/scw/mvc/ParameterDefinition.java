package scw.mvc;

import java.lang.reflect.Type;

import scw.core.AnnotationFactory;

public interface ParameterDefinition extends AnnotationFactory {
	int getParameterCount();

	String getName();

	Class<?> getType();

	Type getGenericType();

	int getIndex();
}
