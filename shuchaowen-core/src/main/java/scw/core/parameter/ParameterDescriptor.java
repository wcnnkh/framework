package scw.core.parameter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import scw.util.value.Value;

public interface ParameterDescriptor{
	public static final ParameterDescriptor[] EMPTY_ARRAY = new ParameterDescriptor[0];
	
	AnnotatedElement getAnnotatedElement();
	
	String getName();
	
	String getDisplayName();

	Class<?> getType();

	Type getGenericType();
	
	Value getDefaultValue();
}
