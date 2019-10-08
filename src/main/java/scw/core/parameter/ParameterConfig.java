package scw.core.parameter;

import java.lang.reflect.Type;

import scw.core.AnnotationFactory;

public interface ParameterConfig extends AnnotationFactory {
	String getName();

	Class<?> getType();

	Type getGenericType();
}
