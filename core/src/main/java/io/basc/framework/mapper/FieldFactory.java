package io.basc.framework.mapper;

import io.basc.framework.lang.Nullable;

@FunctionalInterface
public interface FieldFactory {
	/**
	 * 获取珍上类所有的字段
	 * @param entityClass
	 * @return
	 */
	default Fields getFields(Class<?> entityClass) {
		return getFields(entityClass, null);
	}

	/**
	 * 获取一个类所有的字段
	 * @param entityClass
	 * @param parentField 父级字段
	 * @return
	 */
	Fields getFields(Class<?> entityClass, @Nullable Field parentField);
}
