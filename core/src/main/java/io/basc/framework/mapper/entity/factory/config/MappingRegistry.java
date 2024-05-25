package io.basc.framework.mapper.entity.factory.config;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.entity.FieldDescriptor;
import io.basc.framework.mapper.entity.Mapping;
import io.basc.framework.mapper.entity.factory.MappingFactory;

public interface MappingRegistry extends MappingFactory {
	/**
	 * 是否已注册此实体类的映射
	 * 
	 * @param entityClass
	 * @return
	 */
	boolean isMappingRegistred(Class<?> entityClass);

	/**
	 * 注册一个映射
	 * 
	 * @see #getStructure(Class)
	 * @param entityClass
	 * @param mapping
	 */
	void registerMapping(Class<?> entityClass, Mapping<? extends FieldDescriptor> mapping);

	@Override
	default boolean isEntity(TypeDescriptor source) {
		return MappingFactory.super.isEntity(source) || isMappingRegistred(source.getType());
	}
}
