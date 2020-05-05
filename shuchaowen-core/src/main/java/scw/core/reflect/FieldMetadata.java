package scw.core.reflect;

import scw.core.parameter.ParameterDescriptor;

public interface FieldMetadata extends ParameterDescriptor{
	Class<?> getDeclaringClass();
	
	String getDescription();
	
	boolean equals(Object obj);
	
	int hashCode();
	
	int getModifiers();
}
