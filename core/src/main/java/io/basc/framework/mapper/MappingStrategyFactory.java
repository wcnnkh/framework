package io.basc.framework.mapper;

import io.basc.framework.convert.TypeDescriptor;

public interface MappingStrategyFactory {
	/**
	 * 获取映射策略
	 * 
	 * @param source
	 * @return
	 */
	MappingStrategy getMappingStrategy(TypeDescriptor source);
}
