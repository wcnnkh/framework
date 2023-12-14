package io.basc.framework.mapper;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.StringUtils;
import io.basc.framework.value.Value;

public interface Property extends Value, ParameterDescriptor {

	@Override
	default TypeDescriptor getTypeDescriptor() {
		return Value.super.getTypeDescriptor();
	}

	@Override
	Property rename(String name);

	/**
	 * 是否有效
	 * 
	 * @return
	 */
	default boolean isValid() {
		return StringUtils.isNotEmpty(getName());
	}
}
