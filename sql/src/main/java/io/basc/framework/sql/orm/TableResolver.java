package io.basc.framework.sql.orm;

import java.util.Collection;

import io.basc.framework.mapper.FieldDescriptor;

public interface TableResolver {
	/**
	 * 获取索引信息
	 * 
	 * @param entityClass
	 * @param descriptor
	 * @param resolver
	 * @return
	 */
	Collection<IndexInfo> getIndexs(Class<?> entityClass, FieldDescriptor descriptor);

	String getEngine(Class<?> entityClass);

	String getRowFormat(Class<?> entityClass);
	
	Boolean isAutoCreate(Class<?> entityClass);
}
