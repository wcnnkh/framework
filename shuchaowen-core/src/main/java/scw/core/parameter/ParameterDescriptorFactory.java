package scw.core.parameter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import scw.core.parameter.field.FieldDescriptor;


public interface ParameterDescriptorFactory {
	FieldDescriptor[] getFieldDescriptors(Class<?> clazz);
	
	ParameterDescriptor[] getParameterDescriptors(Constructor<?> constructor);
	
	ParameterDescriptor[] getParameterDescriptors(Method method);
}
