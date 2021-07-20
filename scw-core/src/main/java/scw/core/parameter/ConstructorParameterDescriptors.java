package scw.core.parameter;

import java.lang.reflect.Constructor;

public class ConstructorParameterDescriptors extends DefaultParameterDescriptors<Constructor<?>> {

	public ConstructorParameterDescriptors(ParameterNameDiscoverer parameterNameDiscoverer, Class<?> targetClass,
			Constructor<?> constructor) {
		super(constructor, targetClass, ParameterUtils.getParameters(parameterNameDiscoverer, constructor));
	}

	public ConstructorParameterDescriptors(Class<?> targetClass, Constructor<?> constructor) {
		this(ParameterUtils.getParameterNameDiscoverer(), targetClass, constructor);
	}
}
