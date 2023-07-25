package io.basc.framework.mapper;

import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.support.DefaultObjectMapping;

public interface MappingFactory {
	/**
	 * 获取对应的结构
	 * 
	 * @param entityClass
	 * @return
	 */
	@Nullable
	default Mapping<? extends Element> getMapping(Class<?> entityClass) {
		return DefaultObjectMapping.getMapping(entityClass).all();
	}
}
