package io.basc.framework.mapper;

import io.basc.framework.value.Value;

public interface Setter extends ParameterDescriptor {
	/**
	 * 在指定目标中插入值
	 * 
	 * @param target
	 * @param value
	 */
	void set(Value target, Object value);

	/**
	 * 重命名
	 */
	@Override
	Setter rename(String name);
}