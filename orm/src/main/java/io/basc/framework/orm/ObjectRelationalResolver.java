package io.basc.framework.orm;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.ObjectMapperContext;
import io.basc.framework.util.Range;
import io.basc.framework.util.comparator.Sort;

import java.util.Collection;

public interface ObjectRelationalResolver {
	boolean isIgnore(Class<?> entityClass);

	boolean isIgnore(Class<?> entityClass, ParameterDescriptor descriptor);

	String getName(Class<?> entityClass, ParameterDescriptor descriptor);

	Collection<String> getAliasNames(Class<?> entityClass, ParameterDescriptor descriptor);

	String getName(Class<?> entityClass);

	Collection<String> getAliasNames(Class<?> entityClass);

	boolean isPrimaryKey(Class<?> entityClass, ParameterDescriptor descriptor);

	boolean isNullable(Class<?> entityClass, ParameterDescriptor descriptor);

	boolean isEntity(Class<?> entityClass, ParameterDescriptor descriptor);

	boolean isEntity(Class<?> entityClass);

	boolean isVersionField(Class<?> entityClass, ParameterDescriptor descriptor);

	Collection<Range<Double>> getNumberRanges(Class<?> entityClass, ParameterDescriptor descriptor);

	boolean isAutoIncrement(Class<?> entityClass, ParameterDescriptor descriptor);

	String getComment(Class<?> entityClass);

	String getComment(Class<?> entityClass, ParameterDescriptor descriptor);

	String getCharsetName(Class<?> entityClass);

	String getCharsetName(Class<?> entityClass, ParameterDescriptor descriptor);

	boolean isUnique(Class<?> entityClass, ParameterDescriptor descriptor);

	boolean isIncrement(Class<?> entityClass, ParameterDescriptor descriptor);

	@Nullable
	Sort getSort(Class<?> entityClass, ParameterDescriptor descriptor);

	String getCondition(Class<?> entityClass, ParameterDescriptor descriptor);

	String getRelationship(Class<?> entityClass, ParameterDescriptor descriptor);

	ForeignKey getForeignKey(Class<?> entityClass, ParameterDescriptor descriptor);

	boolean isDisplay(Class<?> entityClass, ParameterDescriptor descriptor);

	boolean isConfigurable(TypeDescriptor sourceType);

	ObjectMapperContext getContext(TypeDescriptor sourceType, ObjectMapperContext parent);
}
