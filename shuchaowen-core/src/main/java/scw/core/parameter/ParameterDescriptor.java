package scw.core.parameter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

public interface ParameterDescriptor{
	AnnotatedElement getAnnotatedElement();
	
	String getName();

	Class<?> getType();

	Type getGenericType();
}
