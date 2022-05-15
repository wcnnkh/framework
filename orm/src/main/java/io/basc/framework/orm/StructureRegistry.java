package io.basc.framework.orm;

import io.basc.framework.lang.Nullable;

public interface StructureRegistry<S extends EntityStructure<? extends Property>> {
	boolean isRegistry(Class<?> entityClass);

	/**
	 * 获取对应的结构
	 * 
	 * @see #isRegistry(Class)
	 * @param entityClass
	 * @return
	 */
	@Nullable
	S getStructure(Class<?> entityClass);

	/**
	 * @see #getStructure(Class)
	 * @param entityClass
	 * @param structure
	 */
	void register(Class<?> entityClass, S structure);
}
