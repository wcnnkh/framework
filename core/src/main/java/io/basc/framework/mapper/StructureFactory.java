package io.basc.framework.mapper;

import java.util.Collection;
import java.util.Map;

import io.basc.framework.core.reflect.ReflectionApi;
import io.basc.framework.lang.Nullable;
import io.basc.framework.value.Value;

public interface StructureFactory {

	boolean isStructureRegistred(Class<?> entityClass);

	/**
	 * 获取对应的结构
	 * 
	 * @see #isRegistry(Class)
	 * @param entityClass
	 * @return
	 */
	@Nullable
	default Structure<? extends Field> getStructure(Class<?> entityClass) {
		return Fields.getFields(entityClass);
	}

	/**
	 * @see #getStructure(Class)
	 * @param entityClass
	 * @param structure
	 */
	void registerStructure(Class<?> entityClass, Structure<? extends Field> structure);

	default Boolean isEntity(Class<?> type) {
		return !Value.isBaseType(type) && type != Object.class && ReflectionApi.isInstance(type)
				&& !Map.class.isAssignableFrom(type) && !Collection.class.isAssignableFrom(type);
	}
}
