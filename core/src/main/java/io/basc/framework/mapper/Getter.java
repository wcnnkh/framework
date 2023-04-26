package io.basc.framework.mapper;

import io.basc.framework.value.Value;

public interface Getter extends ParameterDescriptor {

	/**
	 * 从给定来源中获取值
	 * 
	 * @param source
	 * @return
	 */
	Object get(Value source);

	@Override
	Getter rename(String name);
}
