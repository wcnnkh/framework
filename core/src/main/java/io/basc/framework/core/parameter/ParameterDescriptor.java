package io.basc.framework.core.parameter;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Named;
import io.basc.framework.value.StringValue;
import io.basc.framework.value.Value;

public interface ParameterDescriptor extends AnnotatedElement, Named {
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

	@Nullable
	default Value getDefaultValue() {
		DefaultValue defaultValue = AnnotatedElementUtils.getMergedAnnotation(this, DefaultValue.class);
		if (defaultValue == null) {
			return null;
		}
		return new StringValue(defaultValue.value());
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
}
