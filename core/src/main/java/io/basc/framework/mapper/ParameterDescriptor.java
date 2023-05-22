package io.basc.framework.mapper;

import java.util.function.Predicate;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.annotation.Annotations;
import io.basc.framework.util.Named;

public interface ParameterDescriptor extends Named, Predicate<ParameterDescriptor> {
	public static final ParameterDescriptor[] EMPTY_ARRAY = new ParameterDescriptor[0];

	TypeDescriptor getTypeDescriptor();

	default boolean isNullable() {
		return Annotations.isNullable(this.getTypeDescriptor());
	}

	@Override
	default boolean test(ParameterDescriptor target) {
		if (target == null) {
			return false;
		}

		if (getName().equals(target.getName())) {
			ResolvableType type1 = getTypeDescriptor().getResolvableType();
			ResolvableType type2 = target.getTypeDescriptor().getResolvableType();
			if (type2.isAssignableFrom(type1)) {
				return true;
			}
		}
		return false;
	}
}
