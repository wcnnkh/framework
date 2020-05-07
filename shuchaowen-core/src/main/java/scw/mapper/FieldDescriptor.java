package scw.mapper;

import java.io.Serializable;

import scw.core.parameter.ParameterDescriptor;

public interface FieldDescriptor extends ParameterDescriptor, Serializable{
	Class<?> getDeclaringClass();
	
	boolean equals(Object obj);
	
	int hashCode();
	
	int getModifiers();
}
