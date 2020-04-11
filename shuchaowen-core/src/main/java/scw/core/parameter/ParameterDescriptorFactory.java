package scw.core.parameter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


public interface ParameterDescriptorFactory {
	FieldParameterDescriptor[] getParameterDescriptors(Class<?> clazz);
	
	ParameterDescriptor[] getParameterDescriptors(Constructor<?> constructor);
	
	ParameterDescriptor[] getParameterDescriptors(Method method);
}
