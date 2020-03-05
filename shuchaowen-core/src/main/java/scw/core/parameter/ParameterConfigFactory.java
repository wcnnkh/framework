package scw.core.parameter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


public interface ParameterConfigFactory {
	FieldParameterConfig[] getFieldParameterConfigs(Class<?> clazz);
	
	ParameterConfig[] getParameterConfigs(Constructor<?> constructor);
	
	ParameterConfig[] getParameterConfigs(Method method);
}
