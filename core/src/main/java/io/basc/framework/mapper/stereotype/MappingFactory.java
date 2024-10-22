package io.basc.framework.mapper.stereotype;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.Value;
import io.basc.framework.core.reflect.ReflectionUtils;
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
	Mapping<? extends FieldDescriptor> getMapping(Class<?> entityClass);

	/**
	 * 判断是否是实体对象
	 * 
	 * @param source
	 * @return
	 */
	default boolean isEntity(TypeDescriptor source) {
		return !Value.isBaseType(source.getType()) && !source.isArray() && source.getType() != Object.class
				&& ReflectionUtils.isInstance(source.getType()) && !source.isMap() && !source.isCollection();
	}
}