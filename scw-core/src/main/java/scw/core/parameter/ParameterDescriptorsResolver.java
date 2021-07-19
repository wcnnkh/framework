package scw.core.parameter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public interface ParameterDescriptorsResolver {
	<T> ParameterDescriptors resolve(Class<? extends T> sourceClass,
			Constructor<? extends T> constructor);

	ParameterDescriptors resolve(Class<?> sourceClass, Method method);
}
