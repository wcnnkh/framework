package io.basc.framework.core.execution;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.ParameterDescriptor;
import io.basc.framework.util.Elements;

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
