package io.basc.framework.core.execution;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.ParameterDescriptor;
import io.basc.framework.util.Elements;

public interface Getter extends Executable, ParameterDescriptor {
	@Override
	default Elements<ParameterDescriptor> getParameterDescriptors() {
		return Elements.empty();
	}

	@Override
	default TypeDescriptor getReturnTypeDescriptor() {
		return getTypeDescriptor();
	}

	Object get(Object target);

	@Override
	Getter rename(String name);
}
