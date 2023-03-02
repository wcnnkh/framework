package io.basc.framework.core.parameter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.function.Predicate;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.util.Named;

public interface ParameterDescriptor extends AnnotatedElement, Named, Predicate<ParameterDescriptor> {
	public static final ParameterDescriptor[] EMPTY_ARRAY = new ParameterDescriptor[0];

	Class<?> getType();

	Type getGenericType();

	default boolean isNullable() {
		return AnnotatedElementUtils.isNullable(this);
	}

	default ParameterDescriptor rename(String name) {
		return new OverrideParameterDescriptor(this, name);
	}

	@Override
	default boolean test(ParameterDescriptor target) {
		if (target == null) {
			return false;
		}

		if (getName().equals(target.getName())) {
			ResolvableType type1 = ResolvableType.forType(getGenericType());
			ResolvableType type2 = ResolvableType.forType(target.getGenericType());
			if (type2.isAssignableFrom(type1)) {
				return true;
			}
		}
		return false;
	}
}
