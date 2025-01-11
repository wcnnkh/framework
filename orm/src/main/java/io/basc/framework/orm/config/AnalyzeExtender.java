package io.basc.framework.orm.config;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.Expression;
import io.basc.framework.data.repository.IndexInfo;
import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.data.repository.Sort;
import io.basc.framework.lang.Nullable;
import io.basc.framework.orm.ColumnDescriptor;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.orm.EntityRepository;
import io.basc.framework.orm.ForeignKey;
import io.basc.framework.transform.strategy.PropertiesTransformStrategy;
import io.basc.framework.util.Range;
import io.basc.framework.util.collections.Elements;

public interface AnalyzeExtender {
	default boolean isIgnore(Class<?> entityClass, Analyzer chain) {
		return chain.isIgnore(entityClass);
	}

	default boolean isIgnore(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
		return chain.isIgnore(entityClass, descriptor);
	}

	default String getName(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
		return chain.getName(entityClass, descriptor);
	}

	default Elements<String> getAliasNames(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
		return chain.getAliasNames(entityClass, descriptor);
	}

	default String getName(Class<?> entityClass, Analyzer chain) {
		return chain.getName(entityClass);
	}

	default Elements<String> getAliasNames(Class<?> entityClass, Analyzer chain) {
		return chain.getAliasNames(entityClass);
	}

	default boolean isPrimaryKey(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
		return chain.isPrimaryKey(entityClass, descriptor);
	}

	default boolean isNullable(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
		return chain.isNullable(entityClass, descriptor);
	}

	default boolean isEntity(TypeDescriptor source, ParameterDescriptor descriptor, Analyzer chain) {
		return chain.isEntity(source, descriptor);
	}

	default boolean isEntity(TypeDescriptor source, Analyzer chain) {
		return chain.isEntity(source);
	}

	default boolean isVersion(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
		return chain.isVersion(entityClass, descriptor);
	}

	default Elements<Range<Double>> getNumberRanges(Class<?> entityClass, ParameterDescriptor descriptor,
			Analyzer chain) {
		return chain.getNumberRanges(entityClass, descriptor);
	}

	default boolean isAutoIncrement(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
		return chain.isAutoIncrement(entityClass, descriptor);
	}

	default String getComment(Class<?> entityClass, Analyzer chain) {
		return chain.getComment(entityClass);
	}

	default String getComment(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
		return chain.getComment(entityClass, descriptor);
	}

	default String getCharsetName(Class<?> entityClass, Analyzer chain) {
		return chain.getCharsetName(entityClass);
	}

	default String getCharsetName(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
		return chain.getCharsetName(entityClass, descriptor);
	}

	default boolean isUnique(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
		return chain.isUnique(entityClass, descriptor);
	}

	default boolean isIncrement(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
		return chain.isIncrement(entityClass, descriptor);
	}

	default ForeignKey getForeignKey(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
		return chain.getForeignKey(entityClass, descriptor);
	}

	default boolean isDisplay(Class<?> entityClass, ParameterDescriptor descriptor, Analyzer chain) {
		return chain.isDisplay(entityClass, descriptor);
	}

	default boolean hasEffectiveValue(Parameter parameter, Analyzer chain) {
		return chain.hasEffectiveValue(parameter);
	}

	default PropertiesTransformStrategy getPropertiesTransformStrategy(TypeDescriptor source,
			PropertiesTransformStrategy dottomlessStrategy, Analyzer chain) {
		return chain.getPropertiesTransformStrategy(source, dottomlessStrategy);
	}

	default <T> String getRepositoryName(OperationSymbol operationSymbol, EntityMapping<?> entityMapping,
			Class<? extends T> entityClass, @Nullable T entity, Analyzer chain) {
		return chain.getRepositoryName(operationSymbol, entityMapping, entityClass, entity);
	}

	@Nullable
	default <T> Expression getColumn(OperationSymbol operationSymbol, EntityRepository<T> repository,
			Parameter parameter, @Nullable ColumnDescriptor property, Analyzer chain) {
		return chain.getColumn(operationSymbol, repository, parameter, property);
	}

	@Nullable
	default <T> Condition getCondition(OperationSymbol operationSymbol, EntityRepository<T> repository,
			Parameter parameter, @Nullable ColumnDescriptor property, Analyzer chain) {
		return chain.getCondition(operationSymbol, repository, parameter, property);
	}

	@Nullable
	default <T> Sort getSort(OperationSymbol operationSymbol, EntityRepository<T> repository, Parameter parameter,
			@Nullable ColumnDescriptor property, Analyzer chain) {
		return chain.getSort(operationSymbol, repository, parameter, property);
	}

	default Elements<IndexInfo> getIndexs(Class<?> sourceClass, ParameterDescriptor descriptor, Analyzer chain) {
		return chain.getIndexs(sourceClass, descriptor);
	}
}
