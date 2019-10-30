package scw.core.parameter;

import java.lang.reflect.Type;

import scw.core.reflect.AnnotationFactory;

public interface ParameterConfig extends AnnotationFactory{
	String getName();

	Class<?> getType();

	Type getGenericType();
}
