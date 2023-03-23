package io.basc.framework.orm;

import java.util.Collection;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.ObjectMapperContext;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Range;
import io.basc.framework.util.comparator.Sort;

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

	/**
	 * 是否存在有效值
	 * 
	 * @param entity
	 * @param field
	 * @return
	 */
	default boolean hasEffectiveValue(Object entity, Field field) {
		if (!field.isSupportGetter()) {
			return false;
		}

		Object value = field.get(entity);
		if (value == null) {
			return false;
		}

		Parameter parameter = new Parameter(field.getName(), value, new TypeDescriptor(field.getGetter()));
		if (!parameter.isPresent()) {
			return false;
		}

		return hasEffectiveValue(entity, parameter);
	}

	boolean hasEffectiveValue(Object entity, Parameter parameter);
}
