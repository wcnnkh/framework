package io.basc.framework.execution;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.element.Elements;
import io.basc.framework.value.ParameterDescriptor;

public interface Getter extends Executable, ParameterDescriptor {
	@Override
	default Elements<ParameterDescriptor> getParameterDescriptors() {
		return Elements.empty();
	}

	@Override
	default TypeDescriptor getReturnTypeDescriptor() {
		return getTypeDescriptor();
	}

	Object get(Object target) throws Throwable;
	
	@Override
	Getter rename(String name);
}
