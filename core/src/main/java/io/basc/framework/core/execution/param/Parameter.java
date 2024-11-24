package io.basc.framework.core.execution.param;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.ValueWrapper;

public interface Parameter extends ParameterDescriptor, ValueWrapper {

	@Override
	default TypeDescriptor getTypeDescriptor() {
		return ValueWrapper.super.getTypeDescriptor();
	}

	@Override
	default boolean test(ParameterDescriptor target) {
		if (!ParameterDescriptor.super.test(target)) {
			return false;
		}

		if (!target.isNullable() && !isPresent()) {
			return false;
		}
		return true;
	}
}
