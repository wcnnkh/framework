package io.basc.framework.orm.support;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.data.domain.Range;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.ObjectMapperContext;
import io.basc.framework.orm.ForeignKey;
import io.basc.framework.orm.ObjectRelationalResolver;
import io.basc.framework.util.comparator.Sort;

import java.util.Collection;

public interface ObjectRelationalResolverExtend {
	default boolean isIgnore(Class<?> entityClass, ObjectRelationalResolver chain) {
		return chain.isIgnore(entityClass);
	}

	default boolean isIgnore(Class<?> entityClass, ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.isIgnore(entityClass, descriptor);
	}

	default String getName(Class<?> entityClass, ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.getName(entityClass, descriptor);
	}

	default Collection<String> getAliasNames(Class<?> entityClass, ParameterDescriptor descriptor,
			ObjectRelationalResolver chain) {
		return chain.getAliasNames(entityClass, descriptor);
	}

	default String getName(Class<?> entityClass, ObjectRelationalResolver chain) {
		return chain.getName(entityClass);
	}

	default Collection<String> getAliasNames(Class<?> entityClass, ObjectRelationalResolver chain) {
		return chain.getAliasNames(entityClass);
	}

	default boolean isPrimaryKey(Class<?> entityClass, ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.isPrimaryKey(entityClass, descriptor);
	}

	default boolean isNullable(Class<?> entityClass, ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.isNullable(entityClass, descriptor);
	}

	default boolean isEntity(Class<?> entityClass, ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.isEntity(entityClass, descriptor);
	}

	default boolean isEntity(Class<?> entityClass, ObjectRelationalResolver chain) {
		return chain.isEntity(entityClass);
	}

	default boolean isVersionField(Class<?> entityClass, ParameterDescriptor descriptor,
			ObjectRelationalResolver chain) {
		return chain.isVersionField(entityClass, descriptor);
	}

	/**
	 * 获取值的范围
	 * 
	 * @param entityClass
	 * @param descriptor
	 * @return
	 */
	default Collection<Range<Double>> getNumberRanges(Class<?> entityClass, ParameterDescriptor descriptor,
			ObjectRelationalResolver chain) {
		return chain.getNumberRanges(entityClass, descriptor);
	}

	/**
	 * 是否自增
	 * 
	 * @param entityClass
	 * @param descriptor
	 * @param chain
	 * @return
	 */
	default boolean isAutoIncrement(Class<?> entityClass, ParameterDescriptor descriptor,
			ObjectRelationalResolver chain) {
		return chain.isAutoIncrement(entityClass, descriptor);
	}

	default String getComment(Class<?> entityClass, ObjectRelationalResolver chain) {
		return chain.getComment(entityClass);
	}

	default String getComment(Class<?> entityClass, ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.getComment(entityClass, descriptor);
	}

	default String getCharsetName(Class<?> entityClass, ObjectRelationalResolver chain) {
		return chain.getCharsetName(entityClass);
	}

	default String getCharsetName(Class<?> entityClass, ParameterDescriptor descriptor,
			ObjectRelationalResolver chain) {
		return chain.getCharsetName(entityClass, descriptor);
	}

	default boolean isUnique(Class<?> entityClass, ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.isUnique(entityClass, descriptor);
	}

	default boolean isIncrement(Class<?> entityClass, ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.isIncrement(entityClass, descriptor);
	}

	@Nullable
	default Sort getSort(Class<?> entityClass, ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.getSort(entityClass, descriptor);
	}

	default String getCondition(Class<?> entityClass, ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.getCondition(entityClass, descriptor);
	}

	default String getRelationship(Class<?> entityClass, ParameterDescriptor descriptor,
			ObjectRelationalResolver chain) {
		return chain.getRelationship(entityClass, descriptor);
	}

	default ForeignKey getForeignKey(Class<?> entityClass, ParameterDescriptor descriptor,
			ObjectRelationalResolver chain) {
		return chain.getForeignKey(entityClass, descriptor);
	}

	default boolean isDisplay(Class<?> entityClass, ParameterDescriptor descriptor, ObjectRelationalResolver chain) {
		return chain.isDisplay(entityClass, descriptor);
	}

	default ObjectMapperContext getContext(TypeDescriptor sourceType, ObjectMapperContext parent,
			ObjectRelationalResolver chain) {
		return chain.getContext(sourceType, parent);
	}
	
	default boolean isConfigurable(TypeDescriptor sourceType, ObjectRelationalResolver chain) {
		return chain.isConfigurable(sourceType);
	}
}
