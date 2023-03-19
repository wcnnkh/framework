package io.basc.framework.mapper;

import java.lang.reflect.Type;

import io.basc.framework.core.annotation.AnnotatedElementWrapper;

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

	@Override
	public boolean test(ParameterDescriptor target) {
		return wrappedTarget.test(target);
	}
}
