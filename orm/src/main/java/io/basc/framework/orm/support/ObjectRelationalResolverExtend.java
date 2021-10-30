package io.basc.framework.orm.support;

import java.util.Collection;

import io.basc.framework.data.domain.Range;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.orm.ObjectRelationalResolver;

public interface ObjectRelationalResolverExtend {
	Boolean isIgnore(Class<?> entityClass, ObjectRelationalResolver chain);

	Boolean isIgnore(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain);

	String getName(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain);

	Collection<String> getAliasNames(Class<?> entityClass, FieldDescriptor fieldDescriptor,
			ObjectRelationalResolver chain);

	String getName(Class<?> entityClass, ObjectRelationalResolver chain);

	Collection<String> getAliasNames(Class<?> entityClass, ObjectRelationalResolver chain);

	Boolean isPrimaryKey(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain);

	Boolean isNullable(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain);

	Boolean isEntity(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain);

	Boolean isEntity(Class<?> entityClass, ObjectRelationalResolver chain);

	Boolean isVersionField(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain);

	/**
	 * 获取值的范围
	 * 
	 * @param entityClass
	 * @param descriptor
	 * @return
	 */
	Collection<Range<Double>> getNumberRanges(Class<?> entityClass, FieldDescriptor fieldDescriptor,
			ObjectRelationalResolver chain);

	/**
	 * 是否自增
	 * 
	 * @param entityClass
	 * @param fieldDescriptor
	 * @param chain
	 * @return
	 */
	Boolean isAutoIncrement(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain);

	String getComment(Class<?> entityClass, ObjectRelationalResolver chain);

	String getComment(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain);

	String getCharsetName(Class<?> entityClass, ObjectRelationalResolver chain);

	String getCharsetName(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain);

	Boolean isUnique(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain);

	Boolean isIncrement(Class<?> entityClass, FieldDescriptor fieldDescriptor, ObjectRelationalResolver chain);
}
