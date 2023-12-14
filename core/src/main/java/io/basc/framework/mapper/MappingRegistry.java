package io.basc.framework.mapper;

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
	void registerMapping(Class<?> entityClass, Mapping<? extends Member> mapping);
}
