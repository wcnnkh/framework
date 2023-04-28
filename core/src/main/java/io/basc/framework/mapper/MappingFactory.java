package io.basc.framework.mapper;

import java.util.Collection;
import java.util.Map;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.value.Value;

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
		return ObjectMapping.getMapping(entityClass);
	}

	/**
	 * 注册一个映射
	 * 
	 * @see #getStructure(Class)
	 * @param entityClass
	 * @param structure
	 */
	void registerMapping(Class<?> entityClass, Mapping<? extends Field> structure);

	/**
	 * 判断是否是实体对象
	 * 
	 * @param type
	 * @return
	 */
	default boolean isEntity(Class<?> type) {
		return (!Value.isBaseType(type) && !type.isArray() && type != Object.class && ReflectionUtils.isInstance(type)
				&& !Map.class.isAssignableFrom(type) && !Collection.class.isAssignableFrom(type))
				|| isMappingRegistred(type);
	}
}
