package io.basc.framework.orm.config;

import java.util.Iterator;

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
import io.basc.framework.util.Assert;
import io.basc.framework.util.Range;
import io.basc.framework.util.element.Elements;

public class ChainAnalyzer implements Analyzer {

	public static ChainAnalyzer build(Iterator<AnalyzeExtender> iterator) {
		return new ChainAnalyzer(iterator);
	}

	private final Iterator<AnalyzeExtender> iterator;
	private final Analyzer nextChain;

	public ChainAnalyzer(Iterator<AnalyzeExtender> iterator) {
		this(iterator, null);
	}

	public ChainAnalyzer(Iterator<AnalyzeExtender> iterator, @Nullable Analyzer nextChain) {
		Assert.requiredArgument(iterator != null, "iterator");
		this.iterator = iterator;
		this.nextChain = nextChain;
	}

	public boolean isIgnore(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isIgnore(entityClass, descriptor, this);
		}
		return nextChain == null ? false : nextChain.isIgnore(entityClass, descriptor);
	}

	public String getName(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getName(entityClass, descriptor, this);
		}
		return nextChain == null ? null : nextChain.getName(entityClass, descriptor);
	}

	public Elements<String> getAliasNames(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getAliasNames(entityClass, descriptor, this);
		}
		return nextChain == null ? null : nextChain.getAliasNames(entityClass, descriptor);
	}

	public String getName(Class<?> entityClass) {
		if (iterator.hasNext()) {
			return iterator.next().getName(entityClass, this);
		}
		return nextChain == null ? null : nextChain.getName(entityClass);
	}

	public Elements<String> getAliasNames(Class<?> entityClass) {
		if (iterator.hasNext()) {
			return iterator.next().getAliasNames(entityClass, this);
		}
		return nextChain == null ? null : nextChain.getAliasNames(entityClass);
	}

	public boolean isPrimaryKey(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isPrimaryKey(entityClass, descriptor, this);
		}
		return nextChain == null ? false : nextChain.isPrimaryKey(entityClass, descriptor);
	}

	public boolean isNullable(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isNullable(entityClass, descriptor, this);
		}
		return nextChain == null ? true : nextChain.isNullable(entityClass, descriptor);
	}

	public boolean isEntity(TypeDescriptor source, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isEntity(source, descriptor, this);
		}
		return nextChain == null ? false : nextChain.isEntity(source, descriptor);
	}

	public boolean isEntity(TypeDescriptor source) {
		if (iterator.hasNext()) {
			return iterator.next().isEntity(source, this);
		}
		return nextChain == null ? false : nextChain.isEntity(source);
	}

	public boolean isVersion(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isVersion(entityClass, descriptor, this);
		}
		return nextChain == null ? false : nextChain.isVersion(entityClass, descriptor);
	}

	@Override
	public Elements<Range<Double>> getNumberRanges(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getNumberRanges(entityClass, descriptor, this);
		}
		return nextChain == null ? null : nextChain.getNumberRanges(entityClass, descriptor);
	}

	@Override
	public boolean isAutoIncrement(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isAutoIncrement(entityClass, descriptor, this);
		}
		return nextChain == null ? false : nextChain.isAutoIncrement(entityClass, descriptor);
	}

	@Override
	public String getComment(Class<?> entityClass) {
		if (iterator.hasNext()) {
			return iterator.next().getComment(entityClass, this);
		}
		return nextChain == null ? null : nextChain.getComment(entityClass);
	}

	@Override
	public String getComment(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getComment(entityClass, descriptor, this);
		}
		return nextChain == null ? null : nextChain.getComment(entityClass, descriptor);
	}

	@Override
	public boolean isIgnore(Class<?> entityClass) {
		if (iterator.hasNext()) {
			return iterator.next().isIgnore(entityClass, this);
		}
		return nextChain == null ? false : nextChain.isIgnore(entityClass);
	}

	@Override
	public String getCharsetName(Class<?> entityClass) {
		if (iterator.hasNext()) {
			return iterator.next().getCharsetName(entityClass, this);
		}
		return nextChain == null ? null : nextChain.getCharsetName(entityClass);
	}

	@Override
	public String getCharsetName(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getCharsetName(entityClass, descriptor, this);
		}
		return nextChain == null ? null : nextChain.getCharsetName(entityClass, descriptor);
	}

	@Override
	public boolean isUnique(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isUnique(entityClass, descriptor, this);
		}
		return nextChain == null ? false : nextChain.isUnique(entityClass, descriptor);
	}

	@Override
	public boolean isIncrement(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isIncrement(entityClass, descriptor, this);
		}
		return nextChain == null ? false : nextChain.isIncrement(entityClass, descriptor);
	}

	@Override
	public ForeignKey getForeignKey(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getForeignKey(entityClass, descriptor, this);
		}
		return nextChain == null ? null : nextChain.getForeignKey(entityClass, descriptor);
	}

	@Override
	public boolean isDisplay(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isDisplay(entityClass, descriptor, this);
		}
		return nextChain == null ? false : nextChain.isDisplay(entityClass, descriptor);
	}

	@Override
	public boolean hasEffectiveValue(Parameter parameter) {
		if (iterator.hasNext()) {
			return iterator.next().hasEffectiveValue(parameter, this);
		}

		return nextChain == null ? parameter.isPresent() : nextChain.hasEffectiveValue(parameter);
	}

	@Override
	public PropertiesTransformStrategy getPropertiesTransformStrategy(TypeDescriptor source,
			PropertiesTransformStrategy dottomlessStrategy) {
		if (iterator.hasNext()) {
			return iterator.next().getPropertiesTransformStrategy(source, dottomlessStrategy, this);
		}
		return nextChain == null ? dottomlessStrategy
				: nextChain.getPropertiesTransformStrategy(source, dottomlessStrategy);
	}

	@Override
	public <T> String getRepositoryName(OperationSymbol operationSymbol, EntityMapping<?> entityMapping,
			Class<? extends T> entityClass, T entity) {
		if (iterator.hasNext()) {
			return iterator.next().getRepositoryName(operationSymbol, entityMapping, entityClass, entity, this);
		}
		return nextChain == null ? null
				: nextChain.getRepositoryName(operationSymbol, entityMapping, entityClass, entity);
	}

	@Override
	public <T> Expression getColumn(OperationSymbol operationSymbol, EntityRepository<T> repository,
			Parameter parameter, ColumnDescriptor property) {
		if (iterator.hasNext()) {
			return iterator.next().getColumn(operationSymbol, repository, parameter, property, this);
		}
		return nextChain == null ? null : nextChain.getColumn(operationSymbol, repository, parameter, property);
	}

	@Override
	public <T> Condition getCondition(OperationSymbol operationSymbol, EntityRepository<T> repository,
			Parameter parameter, ColumnDescriptor property) {
		if (iterator.hasNext()) {
			return iterator.next().getCondition(operationSymbol, repository, parameter, property, this);
		}
		return nextChain == null ? null : nextChain.getCondition(operationSymbol, repository, parameter, property);
	}

	@Override
	public <T> Sort getSort(OperationSymbol operationSymbol, EntityRepository<T> repository, Parameter parameter,
			ColumnDescriptor property) {
		if (iterator.hasNext()) {
			return iterator.next().getSort(operationSymbol, repository, parameter, property, this);
		}
		return nextChain == null ? null : nextChain.getSort(operationSymbol, repository, parameter, property);
	}

	@Override
	public Elements<IndexInfo> getIndexs(Class<?> sourceClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getIndexs(sourceClass, descriptor, this);
		}
		return nextChain == null ? Elements.empty() : nextChain.getIndexs(sourceClass, descriptor);
	}

}
