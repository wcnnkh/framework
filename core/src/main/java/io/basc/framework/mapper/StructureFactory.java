package io.basc.framework.mapper;

import java.util.Collection;
import java.util.Map;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.value.Value;

public interface StructureFactory {

	boolean isStructureRegistred(Class<?> entityClass);

	/**
	 * 获取对应的结构
	 * 
	 * @see #isStructureRegistred(Class)
	 * @param entityClass
	 * @return
	 */
	@Nullable
	default Mapping<? extends Field> getStructure(Class<?> entityClass) {
		return Fields.getFields(entityClass);
	}

	/**
	 * @see #getStructure(Class)
	 * @param entityClass
	 * @param structure
	 */
	void registerStructure(Class<?> entityClass, Mapping<? extends Field> structure);

	default boolean isEntity(Class<?> type) {
		return (!Value.isBaseType(type) && !type.isArray() && type != Object.class && ReflectionUtils.isInstance(type)
				&& !Map.class.isAssignableFrom(type) && !Collection.class.isAssignableFrom(type))
				|| isStructureRegistred(type);
	}
}
