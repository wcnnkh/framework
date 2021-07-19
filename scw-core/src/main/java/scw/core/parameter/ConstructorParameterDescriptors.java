package scw.core.parameter;

import java.lang.reflect.Constructor;

import scw.core.annotation.MultiAnnotatedElement;

public class ConstructorParameterDescriptors extends DefaultParameterDescriptors<Constructor<?>> {

	public ConstructorParameterDescriptors(Class<?> targetClass, Constructor<?> constructor) {
		super(targetClass, constructor, ParameterUtils.getParameterNames(constructor),
				constructor.getParameterAnnotations(),
				MultiAnnotatedElement.forAnnotatedElements(constructor, targetClass),
				constructor.getGenericParameterTypes(), constructor.getParameterTypes());
	}

}
