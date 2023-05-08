package io.basc.framework.mapper;

import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.support.DefaultObjectMapping;

public interface MappingFactory {
	/**
	 * 是否已注册此实体类的映射
	 * 
	 * @param entityClass
	 * @return
	 */
	boolean isMappingRegistred(Class<?> entityClass);

	/**
	 * 获取对应的结构
	 * 
	 * @see #isStructureRegistred(Class)
	 * @param entityClass
	 * @return
	 */
	@Nullable
	default Mapping<? extends Field> getMapping(Class<?> entityClass) {
		return DefaultObjectMapping.getMapping(entityClass).all();
	}

	/**
	 * 注册一个映射
	 * 
	 * @see #getStructure(Class)
	 * @param entityClass
	 * @param structure
	 */
	void registerMapping(Class<?> entityClass, Mapping<? extends Field> structure);
}
