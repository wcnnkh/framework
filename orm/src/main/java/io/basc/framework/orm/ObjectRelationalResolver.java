package io.basc.framework.orm;

import io.basc.framework.data.domain.Range;
import io.basc.framework.mapper.FieldDescriptor;

import java.util.Collection;

public interface ObjectRelationalResolver {
	Boolean ignore(Class<?> entityClass, FieldDescriptor fieldDescriptor);

	String getName(Class<?> entityClass, FieldDescriptor fieldDescriptor);

	Collection<String> getAliasNames(Class<?> entityClass,
			FieldDescriptor fieldDescriptor);

	String getName(Class<?> entityClass);

	Collection<String> getAliasNames(Class<?> entityClass);

	Boolean isPrimaryKey(Class<?> entityClass, FieldDescriptor fieldDescriptor);

	Boolean isNullable(Class<?> entityClass, FieldDescriptor fieldDescriptor);

	Boolean isEntity(Class<?> entityClass, FieldDescriptor fieldDescriptor);

	Boolean isEntity(Class<?> entityClass);

	Boolean isVersionField(Class<?> entityClass, FieldDescriptor fieldDescriptor);
	
	/**
	 * 获取值的范围
	 * 
	 * @param entityClass
	 * @param descriptor
	 * @return
	 */
	Range<Double> getNumberRange(Class<?> entityClass, FieldDescriptor fieldDescriptor);
}
