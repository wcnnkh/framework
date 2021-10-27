package io.basc.framework.sql.orm;

import io.basc.framework.mapper.FieldDescriptor;

public interface TableResolver {
	boolean isAutoIncrement(Class<?> entityClass, FieldDescriptor fieldDescriptor);

	/**
	 * 获取索引信息
	 * 
	 * @param entityClass
	 * @param descriptor
	 * @param resolver
	 * @return
	 */
	IndexInfo getIndex(Class<?> entityClass, FieldDescriptor descriptor);
}
