package io.basc.framework.orm.support;

import java.util.Iterator;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.repository.Column;
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
import io.basc.framework.orm.Property;
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

	public boolean isEntity(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isEntity(entityClass, descriptor, this);
		}
		return nextChain == null ? false : nextChain.isEntity(entityClass, descriptor);
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
	public Elements<? extends Sort> getSorts(OperationSymbol operationSymbol, TypeDescriptor source,
			ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getSorts(operationSymbol, source, descriptor, this);
		}
		return nextChain == null ? Elements.empty() : nextChain.getSorts(operationSymbol, source, descriptor);
	}

	@Override
	public Elements<? extends Condition> getConditions(OperationSymbol operationSymbol, TypeDescriptor source,
			Parameter parameter) {
		if (iterator.hasNext()) {
			return iterator.next().getConditions(operationSymbol, source, parameter, this);
		}
		return nextChain == null ? Elements.empty() : nextChain.getConditions(operationSymbol, source, parameter);
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
	public boolean hasEffectiveValue(TypeDescriptor source, Parameter parameter) {
		if (iterator.hasNext()) {
			return iterator.next().hasEffectiveValue(source, parameter, this);
		}

		return nextChain == null ? parameter.isPresent() : nextChain.hasEffectiveValue(source, parameter);
	}

	@Override
	public Elements<? extends Expression> getExpressions(OperationSymbol operationSymbol, TypeDescriptor source,
			Parameter parameter) {
		if (iterator.hasNext()) {
			return iterator.next().getExpressions(operationSymbol, source, parameter, this);
		}
		return nextChain == null ? Elements.empty() : nextChain.getExpressions(operationSymbol, source, parameter);
	}

	@Override
	public Elements<? extends Column> getColumns(OperationSymbol operationSymbol, TypeDescriptor source,
			Parameter parameter) {
		if (iterator.hasNext()) {
			return iterator.next().getColumns(operationSymbol, source, parameter, this);
		}
		return nextChain == null ? Elements.singleton(new Column(parameter))
				: nextChain.getColumns(operationSymbol, source, parameter);
	}

	@Override
	public Elements<? extends Repository> getRepositorys(OperationSymbol operationSymbol,
			TypeDescriptor entityTypeDescriptor, EntityMapping<? extends Property> entityMapping) {
		if (iterator.hasNext()) {
			return iterator.next().getRepositorys(operationSymbol, entityTypeDescriptor, entityMapping, this);
		}
		return nextChain == null ? Elements.singleton(new Repository(entityMapping.getName()))
				: nextChain.getRepositorys(operationSymbol, entityTypeDescriptor, entityMapping);
	}

	@Override
	public MappingStrategy getMappingStrategy(TypeDescriptor source, MappingStrategy dottomlessMappingStrategy) {
		if (iterator.hasNext()) {
			return iterator.next().getMappingStrategy(source, dottomlessMappingStrategy, this);
		}
		return nextChain == null ? dottomlessMappingStrategy
				: nextChain.getMappingStrategy(source, dottomlessMappingStrategy);
	}
}