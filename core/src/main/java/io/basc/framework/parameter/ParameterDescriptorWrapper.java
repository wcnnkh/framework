package io.basc.framework.parameter;

import io.basc.framework.annotation.AnnotatedElementWrapper;
import io.basc.framework.value.Value;

import java.lang.reflect.Type;

public class ParameterDescriptorWrapper<P extends ParameterDescriptor> extends AnnotatedElementWrapper<P>
		implements ParameterDescriptor {

	public ParameterDescriptorWrapper(P target) {
		super(target);
	}

	public String getName() {
		return wrappedTarget.getName();
	}

	public Class<?> getType() {
		return wrappedTarget.getType();
	}

	public Type getGenericType() {
		return wrappedTarget.getGenericType();
	}

	public boolean isNullable() {
		return wrappedTarget.isNullable();
	}

	public Value getDefaultValue() {
		return wrappedTarget.getDefaultValue();
	}
}
