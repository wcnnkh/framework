package io.basc.framework.orm;

import java.util.Collection;

import io.basc.framework.data.domain.Range;
import io.basc.framework.mapper.FieldDescriptor;

public interface ObjectRelationalResolver {
	Boolean isIgnore(Class<?> entityClass);

	Boolean isIgnore(Class<?> entityClass, FieldDescriptor fieldDescriptor);

	String getName(Class<?> entityClass, FieldDescriptor fieldDescriptor);

	Collection<String> getAliasNames(Class<?> entityClass, FieldDescriptor fieldDescriptor);

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
	Collection<Range<Double>> getNumberRanges(Class<?> entityClass, FieldDescriptor fieldDescriptor);

	/**
	 * 是否自增
	 * 
	 * @param entityClass
	 * @param fieldDescriptor
	 * @return
	 */
	Boolean isAutoIncrement(Class<?> entityClass, FieldDescriptor fieldDescriptor);

	String getComment(Class<?> entityClass);

	String getComment(Class<?> entityClass, FieldDescriptor fieldDescriptor);

	String getCharsetName(Class<?> entityClass);

	String getCharsetName(Class<?> entityClass, FieldDescriptor fieldDescriptor);

	Boolean isUnique(Class<?> entityClass, FieldDescriptor fieldDescriptor);

	/**
	 * 是否应该是增量操作 value = value + ?
	 * 
	 * @param entityClass
	 * @param fieldDescriptor
	 * @return
	 */
	Boolean isIncrement(Class<?> entityClass, FieldDescriptor fieldDescriptor);
}
