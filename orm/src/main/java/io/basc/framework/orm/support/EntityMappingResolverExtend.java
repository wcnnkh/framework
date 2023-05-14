package io.basc.framework.orm.support;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.repository.Column;
import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.Expression;
import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.data.repository.Repository;
import io.basc.framework.data.repository.Sort;
import io.basc.framework.mapper.MappingStrategy;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.orm.EntityMappingResolver;
import io.basc.framework.orm.ForeignKey;
import io.basc.framework.orm.Property;
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

	default boolean isEntity(Class<?> entityClass, ParameterDescriptor descriptor, EntityMappingResolver chain) {
		return chain.isEntity(entityClass, descriptor);
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

	default boolean hasEffectiveValue(TypeDescriptor source, Parameter parameter, EntityMappingResolver chain) {
		return chain.hasEffectiveValue(source, parameter);
	}

	default Elements<? extends Expression> getExpressions(OperationSymbol operationSymbol, TypeDescriptor source,
			Parameter parameter, EntityMappingResolver chain) {
		return chain.getExpressions(operationSymbol, source, parameter);
	}

	default Elements<? extends Column> getColumns(OperationSymbol operationSymbol, TypeDescriptor source,
			Parameter parameter, EntityMappingResolver chain) {
		return chain.getColumns(operationSymbol, source, parameter);
	}

	default Elements<? extends Repository> getRepositorys(OperationSymbol operationSymbol,
			TypeDescriptor entityTypeDescriptor, EntityMapping<? extends Property> entityMapping,
			EntityMappingResolver chain) {
		return chain.getRepositorys(operationSymbol, entityTypeDescriptor, entityMapping);
	}

	default Elements<? extends Condition> getConditions(OperationSymbol operationSymbol, TypeDescriptor source,
			Parameter parameter, EntityMappingResolver chain) {
		return chain.getConditions(operationSymbol, source, parameter);
	}

	default Elements<? extends Sort> getSorts(OperationSymbol operationSymbol, TypeDescriptor source,
			ParameterDescriptor descriptor, EntityMappingResolver chain) {
		return chain.getSorts(operationSymbol, source, descriptor);
	}

	default MappingStrategy getMappingStrategy(TypeDescriptor source, MappingStrategy dottomlessMappingStrategy,
			EntityMappingResolver chain) {
		return chain.getMappingStrategy(source, dottomlessMappingStrategy);
	}
}
