package scw.core.parameter;

import java.lang.reflect.Method;

import scw.core.annotation.MultiAnnotatedElement;

public class MethodParameterDescriptors extends DefaultParameterDescriptors<Method> {

	public MethodParameterDescriptors(Class<?> targetClass, Method method) {
		super(targetClass, method, ParameterUtils.getParameterNames(method), method.getParameterAnnotations(),
				MultiAnnotatedElement.forAnnotatedElements(method, targetClass), method.getGenericParameterTypes(),
				method.getParameterTypes());
	}

}
