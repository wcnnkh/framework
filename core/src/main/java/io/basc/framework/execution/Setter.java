package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.param.ParameterDescriptor;
import io.basc.framework.util.element.Elements;

public interface Setter extends Executable, ParameterDescriptor {

	@Override
	default TypeDescriptor getReturnTypeDescriptor() {
		return TypeDescriptor.valueOf(Void.class);
	}

	@Override
	default Elements<ParameterDescriptor> getParameterDescriptors() {
		return Elements.singleton(this);
	}

	void set(Object target, Object value);

	@Override
	Setter rename(String name);
}
