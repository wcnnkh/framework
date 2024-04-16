package io.basc.framework.orm.config;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.Expression;
import io.basc.framework.data.repository.IndexInfo;
import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.data.repository.Sort;
import io.basc.framework.execution.Parameter;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.MappingStrategy;
import io.basc.framework.orm.ColumnDescriptor;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.orm.EntityRepository;
import io.basc.framework.orm.EntityResolver;
import io.basc.framework.orm.ForeignKey;
import io.basc.framework.util.Range;
import io.basc.framework.util.element.Elements;
import io.basc.framework.value.ParameterDescriptor;

public interface EntityResolverExtend {
	default boolean isIgnore(Class<?> entityClass, EntityResolver chain) {
		return chain.isIgnore(entityClass);
	}

	default boolean isIgnore(Class<?> entityClass, ParameterDescriptor descriptor, EntityResolver chain) {
		return chain.isIgnore(entityClass, descriptor);
	}

	default String getName(Class<?> entityClass, ParameterDescriptor descriptor, EntityResolver chain) {
		return chain.getName(entityClass, descriptor);
	}

	default Elements<String> getAliasNames(Class<?> entityClass, ParameterDescriptor descriptor, EntityResolver chain) {
		return chain.getAliasNames(entityClass, descriptor);
	}

	default String getName(Class<?> entityClass, EntityResolver chain) {
		return chain.getName(entityClass);
	}

	default Elements<String> getAliasNames(Class<?> entityClass, EntityResolver chain) {
		return chain.getAliasNames(entityClass);
	}

	default boolean isPrimaryKey(Class<?> entityClass, ParameterDescriptor descriptor, EntityResolver chain) {
		return chain.isPrimaryKey(entityClass, descriptor);
	}

	default boolean isNullable(Class<?> entityClass, ParameterDescriptor descriptor, EntityResolver chain) {
		return chain.isNullable(entityClass, descriptor);
	}

	default boolean isEntity(TypeDescriptor source, ParameterDescriptor descriptor, EntityResolver chain) {
		return chain.isEntity(source, descriptor);
	}

	default boolean isEntity(TypeDescriptor source, EntityResolver chain) {
		return chain.isEntity(source);
	}

	default boolean isVersion(Class<?> entityClass, ParameterDescriptor descriptor, EntityResolver chain) {
		return chain.isVersion(entityClass, descriptor);
	}

	default Elements<Range<Double>> getNumberRanges(Class<?> entityClass, ParameterDescriptor descriptor,
			EntityResolver chain) {
		return chain.getNumberRanges(entityClass, descriptor);
	}

	default boolean isAutoIncrement(Class<?> entityClass, ParameterDescriptor descriptor, EntityResolver chain) {
		return chain.isAutoIncrement(entityClass, descriptor);
	}

	default String getComment(Class<?> entityClass, EntityResolver chain) {
		return chain.getComment(entityClass);
	}

	default String getComment(Class<?> entityClass, ParameterDescriptor descriptor, EntityResolver chain) {
		return chain.getComment(entityClass, descriptor);
	}

	default String getCharsetName(Class<?> entityClass, EntityResolver chain) {
		return chain.getCharsetName(entityClass);
	}

	default String getCharsetName(Class<?> entityClass, ParameterDescriptor descriptor, EntityResolver chain) {
		return chain.getCharsetName(entityClass, descriptor);
	}

	default boolean isUnique(Class<?> entityClass, ParameterDescriptor descriptor, EntityResolver chain) {
		return chain.isUnique(entityClass, descriptor);
	}

	default boolean isIncrement(Class<?> entityClass, ParameterDescriptor descriptor, EntityResolver chain) {
		return chain.isIncrement(entityClass, descriptor);
	}

	default ForeignKey getForeignKey(Class<?> entityClass, ParameterDescriptor descriptor, EntityResolver chain) {
		return chain.getForeignKey(entityClass, descriptor);
	}

	default boolean isDisplay(Class<?> entityClass, ParameterDescriptor descriptor, EntityResolver chain) {
		return chain.isDisplay(entityClass, descriptor);
	}

	default boolean isConfigurable(TypeDescriptor sourceType, EntityResolver chain) {
		return chain.isConfigurable(sourceType);
	}

	default boolean hasEffectiveValue(Parameter parameter, EntityResolver chain) {
		return chain.hasEffectiveValue(parameter);
	}

	default MappingStrategy getMappingStrategy(TypeDescriptor source, MappingStrategy dottomlessMappingStrategy,
			EntityResolver chain) {
		return chain.getMappingStrategy(source, dottomlessMappingStrategy);
	}

	default <T> String getRepositoryName(OperationSymbol operationSymbol, EntityMapping<?> entityMapping,
			Class<? extends T> entityClass, @Nullable T entity, EntityResolver chain) {
		return chain.getRepositoryName(operationSymbol, entityMapping, entityClass, entity);
	}

	@Nullable
	default <T> Expression getColumn(OperationSymbol operationSymbol, EntityRepository<T> repository,
			Parameter parameter, @Nullable ColumnDescriptor property, EntityResolver chain) {
		return chain.getColumn(operationSymbol, repository, parameter, property);
	}

	@Nullable
	default <T> Condition getCondition(OperationSymbol operationSymbol, EntityRepository<T> repository,
			Parameter parameter, @Nullable ColumnDescriptor property, EntityResolver chain) {
		return chain.getCondition(operationSymbol, repository, parameter, property);
	}

	@Nullable
	default <T> Sort getSort(OperationSymbol operationSymbol, EntityRepository<T> repository, Parameter parameter,
			@Nullable ColumnDescriptor property, EntityResolver chain) {
		return chain.getSort(operationSymbol, repository, parameter, property);
	}

	default Elements<IndexInfo> getIndexs(Class<?> sourceClass, ParameterDescriptor descriptor, EntityResolver chain) {
		return chain.getIndexs(sourceClass, descriptor);
	}
}
