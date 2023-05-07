package io.basc.framework.orm;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.Getter;
import io.basc.framework.mapper.MappingStrategyFactory;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Range;
import io.basc.framework.util.comparator.Sort;
import io.basc.framework.value.Value;

public interface EntityMappingResolver extends MappingStrategyFactory {
	boolean isIgnore(Class<?> sourceClass);

	boolean isIgnore(Class<?> sourceClass, ParameterDescriptor descriptor);

	String getName(Class<?> sourceClass, ParameterDescriptor descriptor);

	Elements<String> getAliasNames(Class<?> sourceClass, ParameterDescriptor descriptor);

	String getName(Class<?> sourceClass);

	Elements<String> getAliasNames(Class<?> sourceClass);

	boolean isPrimaryKey(Class<?> sourceClass, ParameterDescriptor descriptor);

	boolean isNullable(Class<?> sourceClass, ParameterDescriptor descriptor);

	boolean isEntity(Class<?> sourceClass, ParameterDescriptor descriptor);

	boolean isEntity(Class<?> sourceClass);

	boolean isVersion(Class<?> sourceClass, ParameterDescriptor descriptor);

	Elements<Range<Double>> getNumberRanges(Class<?> sourceClass, ParameterDescriptor descriptor);

	boolean isAutoIncrement(Class<?> sourceClass, ParameterDescriptor descriptor);

	String getComment(Class<?> sourceClass);

	String getComment(Class<?> sourceClass, ParameterDescriptor descriptor);

	String getCharsetName(Class<?> sourceClass);

	String getCharsetName(Class<?> sourceClass, ParameterDescriptor descriptor);

	boolean isUnique(Class<?> sourceClass, ParameterDescriptor descriptor);

	boolean isIncrement(Class<?> sourceClass, ParameterDescriptor descriptor);

	@Nullable
	Sort getSort(Class<?> sourceClass, ParameterDescriptor descriptor);

	String getCondition(Class<?> sourceClass, ParameterDescriptor descriptor);

	String getRelationship(Class<?> sourceClass, ParameterDescriptor descriptor);

	ForeignKey getForeignKey(Class<?> sourceClass, ParameterDescriptor descriptor);

	boolean isDisplay(Class<?> sourceClass, ParameterDescriptor descriptor);

	boolean isConfigurable(TypeDescriptor source);

	/**
	 * 是否存在有效值
	 * 
	 * @param entity
	 * @param field
	 * @return
	 */
	default boolean hasEffectiveValue(Value source, Field field) {
		if (!field.isSupportGetter()) {
			return false;
		}

		for (Getter getter : field.getGetters()) {
			Object value = getter.get(source);
			if (value == null) {
				continue;
			}

			Parameter parameter = new Parameter(getter.getName(), value, getter.getTypeDescriptor());
			if (!parameter.isPresent()) {
				continue;
			}

			if (hasEffectiveValue(source, field)) {
				return true;
			}
		}
		return false;
	}

	boolean hasEffectiveValue(Value source, Parameter parameter);
}
