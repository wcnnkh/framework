package scw.core.parameter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import scw.core.annotation.MultiAnnotatedElement;

public class DefaultParameterDescriptorsResolver implements
		ParameterDescriptorsResolver {
	private final ParameterNameDiscoverer parameterNameDiscoverer;

	public DefaultParameterDescriptorsResolver(
			ParameterNameDiscoverer parameterNameDiscoverer) {
		this.parameterNameDiscoverer = parameterNameDiscoverer;
	}

	@Override
	public <T> ParameterDescriptors resolve(Class<? extends T> sourceClass,
			Constructor<? extends T> constructor) {
		return new DefaultParameterDescriptors<Constructor<?>>(sourceClass,
				constructor,
				parameterNameDiscoverer.getParameterNames(constructor),
				constructor.getParameterAnnotations(),
				MultiAnnotatedElement.forAnnotatedElements(constructor), constructor.getGenericParameterTypes(),
				constructor.getParameterTypes());
	}

	@Override
	public ParameterDescriptors resolve(Class<?> sourceClass, Method method) {
		return new DefaultParameterDescriptors<Method>(
				sourceClass,
				method,
				parameterNameDiscoverer.getParameterNames(method),
				method.getParameterAnnotations(),
				MultiAnnotatedElement.forAnnotatedElements(method),
				method.getGenericParameterTypes(), method.getParameterTypes());
	}
}
