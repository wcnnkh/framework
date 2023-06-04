package io.basc.framework.mapper;

public interface Setter extends ParameterDescriptor {
	/**
	 * 在指定目标中插入值
	 * 
	 * @param target
	 * @param value
	 */
	void set(Object target, Object value);

	Setter rename(String name);
}