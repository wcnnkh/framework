package io.basc.framework.orm.config;

import java.util.Iterator;

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
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Range;

public class EntityMappingResolverExtendChain implements EntityMappingResolver {

	public static EntityMappingResolverExtendChain build(Iterator<EntityMappingResolverExtend> iterator) {
		return new EntityMappingResolverExtendChain(iterator);
	}

	private final Iterator<EntityMappingResolverExtend> iterator;
	private final EntityMappingResolver nextChain;

	public EntityMappingResolverExtendChain(Iterator<EntityMappingResolverExtend> iterator) {
		this(iterator, null);
	}

	public EntityMappingResolverExtendChain(Iterator<EntityMappingResolverExtend> iterator,
			@Nullable EntityMappingResolver nextChain) {
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
	public boolean isConfigurable(TypeDescriptor sourceType) {
		if (iterator.hasNext()) {
			return iterator.next().isConfigurable(sourceType, this);
		}
		return nextChain == null ? false : nextChain.isConfigurable(sourceType);
	}

	@Override
	public boolean hasEffectiveValue(Parameter parameter) {
		if (iterator.hasNext()) {
			return iterator.next().hasEffectiveValue(parameter, this);
		}

		return nextChain == null ? parameter.isPresent() : nextChain.hasEffectiveValue(parameter);
	}

	@Override
	public MappingStrategy getMappingStrategy(TypeDescriptor source, MappingStrategy dottomlessMappingStrategy) {
		if (iterator.hasNext()) {
			return iterator.next().getMappingStrategy(source, dottomlessMappingStrategy, this);
		}
		return nextChain == null ? dottomlessMappingStrategy
				: nextChain.getMappingStrategy(source, dottomlessMappingStrategy);
	}

	@Override
	public Expression toColumn(OperationSymbol operationSymbol, Repository repository, Class<?> entityClass,
			EntityMapping<?> entityMapping, Parameter parameter) {
		if (iterator.hasNext()) {
			return iterator.next().toColumn(operationSymbol, repository, entityClass, entityMapping, parameter, this);
		}
		return nextChain == null ? null
				: nextChain.toColumn(operationSymbol, repository, entityClass, entityMapping, parameter);
	}

	@Override
	public Condition toCondition(OperationSymbol operationSymbol, Repository repository, Class<?> entityClass,
			EntityMapping<?> entityMapping, Parameter parameter) {
		if (iterator.hasNext()) {
			return iterator.next().toCondition(operationSymbol, repository, entityClass, entityMapping, parameter,
					this);
		}
		return nextChain == null ? null
				: nextChain.toCondition(operationSymbol, repository, entityClass, entityMapping, parameter);
	}

	@Override
	public Sort toSort(OperationSymbol operationSymbol, Repository repository, Class<?> entityClass,
			EntityMapping<?> entityMapping, Parameter parameter) {
		if (iterator.hasNext()) {
			return iterator.next().toSort(operationSymbol, repository, entityClass, entityMapping, parameter, this);
		}
		return nextChain == null ? null
				: nextChain.toSort(operationSymbol, repository, entityClass, entityMapping, parameter);
	}

	@Override
	public Repository getRepository(OperationSymbol operationSymbol, Class<?> entityClass,
			EntityMapping<?> entityMapping, Object entity) {
		if (iterator.hasNext()) {
			return iterator.next().getRepository(operationSymbol, entityClass, entityMapping, entity, this);
		}
		return nextChain == null ? null : nextChain.getRepository(operationSymbol, entityClass, entityMapping, entity);
	}
}
