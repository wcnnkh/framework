package scw.core.parameter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

public interface ParameterDescriptor{
	public static final ParameterDescriptor[] EMPTY_ARRAY = new ParameterDescriptor[0];
	
	AnnotatedElement getAnnotatedElement();
	
	String getName();

	Class<?> getType();

	Type getGenericType();
}
