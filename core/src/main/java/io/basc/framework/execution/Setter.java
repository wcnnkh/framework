package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.element.Elements;
import io.basc.framework.value.ParameterDescriptor;

public interface Setter extends Executable, ParameterDescriptor {

	@Override
	default TypeDescriptor getReturnTypeDescriptor() {
		return TypeDescriptor.valueOf(Void.class);
	}

	@Override
	default Elements<ParameterDescriptor> getParameterDescriptors() {
		return Elements.singleton(this);
	}

	void set(Object target, Object value) throws Throwable;

	@Override
	Setter rename(String name);
}
