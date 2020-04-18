package scw.core.instance;

import java.lang.reflect.Constructor;

import scw.core.parameter.ParameterDescriptor;

public class ConstructorDescriptor {
	private final Constructor<?> constructor;
	private final ParameterDescriptor[] parameterDescriptors;

	public ConstructorDescriptor(Constructor<?> constructor,
			ParameterDescriptor[] parameterDescriptors) {
		this.constructor = constructor;
		this.parameterDescriptors = parameterDescriptors;
	}

	public Constructor<?> getConstructor() {
		return constructor;
	}

	public ParameterDescriptor[] getParameterDescriptors() {
		return parameterDescriptors;
	}
}
