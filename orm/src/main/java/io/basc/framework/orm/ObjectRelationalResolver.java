package io.basc.framework.orm;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.data.domain.Range;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.comparator.Sort;

import java.util.Collection;

public interface ObjectRelationalResolver {
	Boolean isIgnore(Class<?> entityClass);

	Boolean isIgnore(Class<?> entityClass, ParameterDescriptor descriptor);

	String getName(Class<?> entityClass, ParameterDescriptor descriptor);

	Collection<String> getAliasNames(Class<?> entityClass, ParameterDescriptor descriptor);

	String getName(Class<?> entityClass);

	Collection<String> getAliasNames(Class<?> entityClass);

	Boolean isPrimaryKey(Class<?> entityClass, ParameterDescriptor descriptor);

	Boolean isNullable(Class<?> entityClass, ParameterDescriptor descriptor);

	Boolean isEntity(Class<?> entityClass, ParameterDescriptor descriptor);

	Boolean isEntity(Class<?> entityClass);

	Boolean isVersionField(Class<?> entityClass, ParameterDescriptor descriptor);

	/**
	 * 获取值的范围
	 * 
	 * @param entityClass
	 * @param descriptor
	 * @return
	 */
	Collection<Range<Double>> getNumberRanges(Class<?> entityClass, ParameterDescriptor descriptor);

	/**
	 * 是否自增
	 * 
	 * @param entityClass
	 * @param descriptor
	 * @return
	 */
	Boolean isAutoIncrement(Class<?> entityClass, ParameterDescriptor descriptor);

	String getComment(Class<?> entityClass);

	String getComment(Class<?> entityClass, ParameterDescriptor descriptor);

	String getCharsetName(Class<?> entityClass);

	String getCharsetName(Class<?> entityClass, ParameterDescriptor descriptor);

	Boolean isUnique(Class<?> entityClass, ParameterDescriptor descriptor);

	/**
	 * 是否应该是增量操作 value = value + ?
	 * 
	 * @param entityClass
	 * @param descriptor
	 * @return
	 */
	Boolean isIncrement(Class<?> entityClass, ParameterDescriptor descriptor);

	@Nullable
	Sort getSort(Class<?> entityClass, ParameterDescriptor descriptor);

	String getCondition(Class<?> entityClass, ParameterDescriptor descriptor);

	String getRelationship(Class<?> entityClass, ParameterDescriptor descriptor);

	/**
	 * 获取外键对应的实体类
	 * 
	 * @param entityClass
	 * @param descriptor
	 * @return
	 */
	ForeignKey getForeignKey(Class<?> entityClass, ParameterDescriptor descriptor);

	boolean isDisplay(Class<?> entityClass, ParameterDescriptor descriptor);
}
