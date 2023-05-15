package io.basc.framework.mapper;

public interface Getter extends ParameterDescriptor {

	/**
	 * 从给定来源中获取值
	 * 
	 * @param source
	 * @return
	 */
	Object get(Object source);

	@Override
	Getter rename(String name);
}
