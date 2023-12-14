package io.basc.framework.mapper;

import io.basc.framework.lang.Nullable;

@FunctionalInterface
public interface MappingFactory {
	/**
	 * 获取对应的结构
	 * 
	 * @param entityClass
	 * @return
	 */
	@Nullable
	Mapping<? extends Member> getMapping(Class<?> entityClass);
}
