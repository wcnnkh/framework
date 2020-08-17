package scw.core.parameter;

import java.lang.reflect.Constructor;

public class ConstructorParameterDescriptors extends DefaultParameterDescriptors<Constructor<?>>{

	public ConstructorParameterDescriptors(Class<?> targetClass,
			Constructor<?> constructor) {
		super(targetClass, constructor, ParameterUtils.getParameterNames(constructor), constructor.getParameterAnnotations(), constructor.getGenericParameterTypes(), constructor.getParameterTypes());
	}

}
