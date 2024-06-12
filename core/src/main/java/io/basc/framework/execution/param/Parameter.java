package io.basc.framework.execution.param;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.Value;

public interface Parameter extends ParameterDescriptor, Value {

	@Override
	default TypeDescriptor getTypeDescriptor() {
		return Value.super.getTypeDescriptor();
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
