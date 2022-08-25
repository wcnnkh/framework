package io.basc.framework.orm.support;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.data.domain.Range;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.ObjectMapperContext;
import io.basc.framework.orm.ForeignKey;
import io.basc.framework.orm.ObjectRelationalResolver;
import io.basc.framework.util.Assert;
import io.basc.framework.util.comparator.Sort;

import java.util.Collection;
import java.util.Iterator;

public class ObjectRelationalResolverExtendChain implements ObjectRelationalResolver {

	public static ObjectRelationalResolverExtendChain build(Iterator<ObjectRelationalResolverExtend> iterator) {
		return new ObjectRelationalResolverExtendChain(iterator);
	}

	private final Iterator<ObjectRelationalResolverExtend> iterator;
	private final ObjectRelationalResolver nextChain;

	public ObjectRelationalResolverExtendChain(Iterator<ObjectRelationalResolverExtend> iterator) {
		this(iterator, null);
	}

	public ObjectRelationalResolverExtendChain(Iterator<ObjectRelationalResolverExtend> iterator,
			@Nullable ObjectRelationalResolver nextChain) {
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

	public Collection<String> getAliasNames(Class<?> entityClass, ParameterDescriptor descriptor) {
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

	public Collection<String> getAliasNames(Class<?> entityClass) {
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

	public boolean isEntity(Class<?> entityClass) {
		if (iterator.hasNext()) {
			return iterator.next().isEntity(entityClass, this);
		}
		return nextChain == null ? false : nextChain.isEntity(entityClass);
	}

	public boolean isVersionField(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isVersionField(entityClass, descriptor, this);
		}
		return nextChain == null ? false : nextChain.isVersionField(entityClass, descriptor);
	}

	@Override
	public Collection<Range<Double>> getNumberRanges(Class<?> entityClass, ParameterDescriptor descriptor) {
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
	public Sort getSort(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getSort(entityClass, descriptor, this);
		}
		return nextChain == null ? null : nextChain.getSort(entityClass, descriptor);
	}

	@Override
	public String getCondition(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getCondition(entityClass, descriptor, this);
		}
		return nextChain == null ? null : nextChain.getCondition(entityClass, descriptor);
	}

	@Override
	public String getRelationship(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getRelationship(entityClass, descriptor, this);
		}
		return nextChain == null ? null : nextChain.getRelationship(entityClass, descriptor);
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
	public ObjectMapperContext getContext(TypeDescriptor sourceType, ObjectMapperContext parent) {
		if (iterator.hasNext()) {
			return iterator.next().getContext(sourceType, parent, this);
		}
		return nextChain == null ? new ObjectMapperContext(parent) : nextChain.getContext(sourceType, parent);
	}

	@Override
	public boolean isConfigurable(TypeDescriptor sourceType) {
		if (iterator.hasNext()) {
			return iterator.next().isConfigurable(sourceType, this);
		}
		return nextChain == null ? false : nextChain.isConfigurable(sourceType);
	}
}
