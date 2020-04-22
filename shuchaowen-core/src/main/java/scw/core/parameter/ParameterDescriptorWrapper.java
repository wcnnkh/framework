package scw.core.parameter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import scw.util.value.Value;

public class ParameterDescriptorWrapper implements ParameterDescriptor {
	protected final ParameterDescriptor parameterDescriptor;

	public ParameterDescriptorWrapper(ParameterDescriptor parameterDescriptor) {
		this.parameterDescriptor = parameterDescriptor;
	}

	public AnnotatedElement getAnnotatedElement() {
		return parameterDescriptor.getAnnotatedElement();
	}

	public String getName() {
		return parameterDescriptor.getName();
	}

	public String getDisplayName() {
		return parameterDescriptor.getDisplayName();
	}

	public Class<?> getType() {
		return parameterDescriptor.getType();
	}

	public Type getGenericType() {
		return parameterDescriptor.getGenericType();
	}

	public Value getDefaultValue() {
		return parameterDescriptor.getDefaultValue();
	}
}