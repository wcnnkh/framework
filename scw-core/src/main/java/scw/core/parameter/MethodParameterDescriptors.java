package scw.core.parameter;

import java.lang.reflect.Method;

public class MethodParameterDescriptors extends DefaultParameterDescriptors<Method> {

	public MethodParameterDescriptors(Class<?> targetClass, Method method) {
		super(targetClass, method, ParameterUtils.getParameterNames(method), method.getParameterAnnotations(), method,
				method.getGenericParameterTypes(), method.getParameterTypes());
	}

}
