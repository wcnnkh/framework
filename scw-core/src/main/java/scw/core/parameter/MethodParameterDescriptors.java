package scw.core.parameter;

import java.lang.reflect.Method;

public class MethodParameterDescriptors extends DefaultParameterDescriptors<Method> {

	public MethodParameterDescriptors(Class<?> targetClass, Method method) {
		this(ParameterUtils.getParameterNameDiscoverer(), targetClass, method);
	}

	public MethodParameterDescriptors(ParameterNameDiscoverer parameterNameDiscoverer, Class<?> targetClass,
			Method method) {
		super(method, targetClass, ParameterUtils.getParameters(parameterNameDiscoverer, method));
	}
}
