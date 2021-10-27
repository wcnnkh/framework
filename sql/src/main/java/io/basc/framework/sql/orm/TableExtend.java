package io.basc.framework.sql.orm;

import io.basc.framework.data.domain.Range;
import io.basc.framework.mapper.FieldDescriptor;

public interface TableExtend {
	/**
	 * 获取值的范围
	 * @param entityClass
	 * @param descriptor
	 * @return
	 */
	Range<Double> getRange(Class<?> entityClass, FieldDescriptor descriptor, TableResolver resolver);

	/**
	 * 获取索引信息
	 * @param entityClass
	 * @param descriptor
	 * @param resolver
	 * @return
	 */
	IndexInfo getIndex(Class<?> entityClass, FieldDescriptor descriptor, TableResolver resolver);
}
