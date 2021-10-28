package io.basc.framework.sql.orm.support;

import java.util.Collection;

import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.sql.orm.IndexInfo;
import io.basc.framework.sql.orm.TableResolver;

public interface TableResolverExtend {

	/**
	 * 获取索引信息
	 * 
	 * @param entityClass
	 * @param descriptor
	 * @param resolver
	 * @return
	 */
	Collection<IndexInfo> getIndexs(Class<?> entityClass, FieldDescriptor descriptor, TableResolver chain);

	String getEngine(Class<?> entityClass, TableResolver chain);

	String getRowFormat(Class<?> entityClass, TableResolver chain);
}
