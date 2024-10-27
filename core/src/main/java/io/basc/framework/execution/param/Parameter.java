package io.basc.framework.execution.param;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ObjectValue;

public interface Parameter extends ParameterDescriptor, ObjectValue {

	@Override
	default TypeDescriptor getTypeDescriptor() {
		return ObjectValue.super.getTypeDescriptor();
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
