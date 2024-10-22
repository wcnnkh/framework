package io.basc.framework.orm.config;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.repository.Condition;
import io.basc.framework.data.repository.Expression;
import io.basc.framework.data.repository.IndexInfo;
import io.basc.framework.data.repository.OperationSymbol;
import io.basc.framework.data.repository.Sort;
import io.basc.framework.execution.param.Parameter;
import io.basc.framework.execution.param.ParameterDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.orm.ColumnDescriptor;
import io.basc.framework.orm.EntityMapping;
import io.basc.framework.orm.EntityRepository;
import io.basc.framework.orm.ForeignKey;
import io.basc.framework.transform.strategy.PropertiesTransformStrategy;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Range;

public interface Analyzer {
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

	boolean hasEffectiveValue(Parameter parameter);

	PropertiesTransformStrategy getPropertiesTransformStrategy(TypeDescriptor source,
			PropertiesTransformStrategy dottomlessStrategy);

	<T> String getRepositoryName(OperationSymbol operationSymbol, EntityMapping<?> entityMapping,
			Class<? extends T> entityClass, @Nullable T entity);

	@Nullable
	<T> Expression getColumn(OperationSymbol operationSymbol, EntityRepository<T> repository, Parameter parameter,
			@Nullable ColumnDescriptor property);

	@Nullable
	<T> Condition getCondition(OperationSymbol operationSymbol, EntityRepository<T> repository, Parameter parameter,
			@Nullable ColumnDescriptor property);

	@Nullable
	<T> Sort getSort(OperationSymbol operationSymbol, EntityRepository<T> repository, Parameter parameter,
			@Nullable ColumnDescriptor property);

	Elements<IndexInfo> getIndexs(Class<?> sourceClass, ParameterDescriptor descriptor);
}