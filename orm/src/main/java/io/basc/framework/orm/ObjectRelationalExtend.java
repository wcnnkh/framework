package io.basc.framework.orm;

import io.basc.framework.data.domain.Range;
import io.basc.framework.mapper.FieldDescriptor;

import java.util.Collection;

public interface ObjectRelationalExtend {
	Boolean ignore(Class<?> entityClass, FieldDescriptor fieldDescriptor,
			ObjectRelationalResolver chain);

	String getName(Class<?> entityClass, FieldDescriptor fieldDescriptor,
			ObjectRelationalResolver chain);

	Collection<String> getAliasNames(Class<?> entityClass,
			FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain);

	String getName(Class<?> entityClass, ObjectRelationalResolver chain);

	Collection<String> getAliasNames(Class<?> entityClass,
			ObjectRelationalResolver chain);

	Boolean isPrimaryKey(Class<?> entityClass, FieldDescriptor fieldDescriptor,
			ObjectRelationalResolver chain);

	Boolean isNullable(Class<?> entityClass, FieldDescriptor fieldDescriptor,
			ObjectRelationalResolver chain);

	Boolean isEntity(Class<?> entityClass, FieldDescriptor fieldDescriptor,
			ObjectRelationalResolver chain);

	Boolean isEntity(Class<?> entityClass, ObjectRelationalResolver chain);

	Boolean isVersionField(Class<?> entityClass,
			FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain);
	
	/**
	 * 获取值的范围
	 * 
	 * @param entityClass
	 * @param descriptor
	 * @return
	 */
	Range<Double> getNumberRange(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain);
}
