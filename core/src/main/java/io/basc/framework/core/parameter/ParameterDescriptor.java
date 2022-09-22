package io.basc.framework.core.parameter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.util.Accept;
import io.basc.framework.util.Named;

public interface ParameterDescriptor extends AnnotatedElement, Named, Accept<ParameterDescriptor> {
	public static final ParameterDescriptor[] EMPTY_ARRAY = new ParameterDescriptor[0];

	Class<?> getType();

	Type getGenericType();

	/**
	 * 是否可以为空
	 * 
	 * @return
	 */
	default boolean isNullable() {
		return AnnotatedElementUtils.isNullable(this);
	}

	/**
	 * 重命名
	 * 
	 * @param name
	 * @return
	 */
	default ParameterDescriptor rename(String name) {
		return new OverrideParameterDescriptor(this, name);
	}

	@Override
	default boolean accept(ParameterDescriptor target) {
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
