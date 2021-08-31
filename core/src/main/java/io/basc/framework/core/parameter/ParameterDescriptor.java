package io.basc.framework.core.parameter;

import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Named;
import io.basc.framework.value.StringValue;
import io.basc.framework.value.Value;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

public interface ParameterDescriptor extends AnnotatedElement, Named {
	public static final ParameterDescriptor[] EMPTY_ARRAY = new ParameterDescriptor[0];

	String getName();

	Class<?> getType();

	Type getGenericType();

	/**
	 * 是否可以为空
	 * 
	 * @return
	 */
	boolean isNullable();

	@Nullable
	default Value getDefaultValue() {
		String defaultValue = AnnotatedElementUtils.getDefaultValue(this);
		if (defaultValue == null) {
			return null;
		}
		return new StringValue(defaultValue);
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
