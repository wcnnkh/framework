package io.basc.framework.orm.config;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.Expression;
import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.data.repository.Repository;
import io.basc.framework.data.repository.Sort;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.MappingStrategy;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.orm.EntityMappingResolver;
import io.basc.framework.orm.ForeignKey;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Range;

public interface EntityMappingResolverExtend {
	default boolean isIgnore(Class<?> entityClass, EntityMappingResolver chain) {
		return chain.isIgnore(entityClass);
	}

	default boolean isIgnore(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		return chain.isIgnore(entityClass, descriptor);
	}

	default String getName(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		return chain.getName(entityClass, descriptor);
	}

	default Elements<String> getAliasNames(Class<?> entityClass, ParameterDescriptor descriptor,
			EntityMappingResolver chain) {
		return chain.getAliasNames(entityClass, descriptor);
	}

	default String getName(Class<?> entityClass, EntityMappingResolver chain) {
		return chain.getName(entityClass);
	}

	default Elements<String> getAliasNames(Class<?> entityClass, EntityMappingResolver chain) {
		return chain.getAliasNames(entityClass);
	}

	default boolean isPrimaryKey(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		return chain.isPrimaryKey(entityClass, descriptor);
	}

	default boolean isNullable(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		return chain.isNullable(entityClass, descriptor);
	}

	default boolean isEntity(TypeDescriptor source, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		return chain.isEntity(source, descriptor);
	}

	default boolean isEntity(TypeDescriptor source, EntityMappingResolver chain) {
		return chain.isEntity(source);
	}

	default boolean isVersion(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		return chain.isVersion(entityClass, descriptor);
	}

	default Elements<Range<Double>> getNumberRanges(Class<?> entityClass, ParameterDescriptor descriptor,
			EntityMappingResolver chain) {
		return chain.getNumberRanges(entityClass, descriptor);
	}

	default boolean isAutoIncrement(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		return chain.isAutoIncrement(entityClass, descriptor);
	}

	default String getComment(Class<?> entityClass, EntityMappingResolver chain) {
		return chain.getComment(entityClass);
	}

	default String getComment(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		return chain.getComment(entityClass, descriptor);
	}

	default String getCharsetName(Class<?> entityClass, EntityMappingResolver chain) {
		return chain.getCharsetName(entityClass);
	}

	default String getCharsetName(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		return chain.getCharsetName(entityClass, descriptor);
	}

	default boolean isUnique(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		return chain.isUnique(entityClass, descriptor);
	}

	default boolean isIncrement(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		return chain.isIncrement(entityClass, descriptor);
	}

	default ForeignKey getForeignKey(Class<?> entityClass, ParameterDescriptor descriptor,
			EntityMappingResolver chain) {
		return chain.getForeignKey(entityClass, descriptor);
	}

	default boolean isDisplay(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		return chain.isDisplay(entityClass, descriptor);
	}

	default boolean isConfigurable(TypeDescriptor sourceType, EntityMappingResolver chain) {
		return chain.isConfigurable(sourceType);
	}

	default boolean hasEffectiveValue(Parameter parameter, EntityMappingResolver chain) {
		return chain.hasEffectiveValue(parameter);
	}

	default MappingStrategy getMappingStrategy(TypeDescriptor source, MappingStrategy dottomlessMappingStrategy,
			EntityMappingResolver chain) {
		return chain.getMappingStrategy(source, dottomlessMappingStrategy);
	}

	default Repository getRepository(OperationSymbol operationSymbol, Class<?> entityClass,
			EntityMapping<?> entityMapping, @Nullable Object entity, EntityMappingResolver chain) {
		return chain.getRepository(operationSymbol, entityClass, entityMapping, entity);
	}

	default Expression toColumn(OperationSymbol operationSymbol, Repository repository, Class<?> entityClass,
			EntityMapping<?> entityMapping, Parameter parameter, EntityMappingResolver chain) {
		return chain.toColumn(operationSymbol, repository, entityClass, entityMapping, parameter);
	}

	default Condition toCondition(OperationSymbol operationSymbol, Repository repository, Class<?> entityClass,
			EntityMapping<?> entityMapping, Parameter parameter, EntityMappingResolver chain) {
		return chain.toCondition(operationSymbol, repository, entityClass, entityMapping, parameter);
	}

	default Sort toSort(OperationSymbol operationSymbol, Repository repository, Class<?> entityClass,
			EntityMapping<?> entityMapping, Parameter parameter, EntityMappingResolver chain) {
		return chain.toSort(operationSymbol, repository, entityClass, entityMapping, parameter);
	}
}
