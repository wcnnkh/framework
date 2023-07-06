package io.basc.framework.orm;

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
import io.basc.framework.util.Elements;
import io.basc.framework.util.Range;

public interface EntityMappingResolver {
	boolean isIgnore(Class<?> sourceClass);

	boolean isIgnore(Class<?> sourceClass, ParameterDescriptor descriptor);

	String getName(Class<?> sourceClass, ParameterDescriptor descriptor);

	Elements<String> getAliasNames(Class<?> sourceClass, ParameterDescriptor descriptor);

	String getName(Class<?> sourceClass);

	Elements<String> getAliasNames(Class<?> sourceClass);

	boolean isPrimaryKey(Class<?> sourceClass, ParameterDescriptor descriptor);

	boolean isNullable(Class<?> sourceClass, ParameterDescriptor descriptor);

	boolean isEntity(TypeDescriptor source, ParameterDescriptor descriptor);

	boolean isEntity(TypeDescriptor source);

	boolean isVersion(Class<?> sourceClass, ParameterDescriptor descriptor);

	Elements<Range<Double>> getNumberRanges(Class<?> sourceClass, ParameterDescriptor descriptor);

	boolean isAutoIncrement(Class<?> sourceClass, ParameterDescriptor descriptor);

	String getComment(Class<?> sourceClass);

	String getComment(Class<?> sourceClass, ParameterDescriptor descriptor);

	String getCharsetName(Class<?> sourceClass);

	String getCharsetName(Class<?> sourceClass, ParameterDescriptor descriptor);

	boolean isUnique(Class<?> sourceClass, ParameterDescriptor descriptor);

	boolean isIncrement(Class<?> sourceClass, ParameterDescriptor descriptor);

	ForeignKey getForeignKey(Class<?> sourceClass, ParameterDescriptor descriptor);

	boolean isDisplay(Class<?> sourceClass, ParameterDescriptor descriptor);

	boolean isConfigurable(TypeDescriptor source);

	boolean hasEffectiveValue(Parameter parameter);

	MappingStrategy getMappingStrategy(TypeDescriptor source, MappingStrategy dottomlessMappingStrategy);

	Repository getRepository(OperationSymbol operationSymbol, Class<?> entityClass, EntityMapping<?> entityMapping,
			@Nullable Object entity);

	Expression toColumn(OperationSymbol operationSymbol, Repository repository, Class<?> entityClass,
			EntityMapping<?> entityMapping, Parameter parameter);

	Condition toCondition(OperationSymbol operationSymbol, Repository repository, Class<?> entityClass,
			EntityMapping<?> entityMapping, Parameter parameter);

	Sort toSort(OperationSymbol operationSymbol, Repository repository, Class<?> entityClass,
			EntityMapping<?> entityMapping, Parameter parameter);
}
