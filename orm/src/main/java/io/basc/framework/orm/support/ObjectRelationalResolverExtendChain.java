package io.basc.framework.orm.support;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.data.domain.Range;
import io.basc.framework.lang.Nullable;
import io.basc.framework.orm.ObjectRelationalResolver;
import io.basc.framework.util.Assert;
import io.basc.framework.util.comparator.Sort;

import java.util.Collection;
import java.util.Iterator;

public class ObjectRelationalResolverExtendChain implements
		ObjectRelationalResolver {

	public static ObjectRelationalResolverExtendChain build(
			Iterator<ObjectRelationalResolverExtend> iterator) {
		return new ObjectRelationalResolverExtendChain(iterator);
	}

	private final Iterator<ObjectRelationalResolverExtend> iterator;
	private final ObjectRelationalResolver nextChain;

	public ObjectRelationalResolverExtendChain(
			Iterator<ObjectRelationalResolverExtend> iterator) {
		this(iterator, null);
	}

	public ObjectRelationalResolverExtendChain(
			Iterator<ObjectRelationalResolverExtend> iterator,
			@Nullable ObjectRelationalResolver nextChain) {
		Assert.requiredArgument(iterator != null, "iterator");
		this.iterator = iterator;
		this.nextChain = nextChain;
	}

	public Boolean isIgnore(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isIgnore(entityClass, descriptor, this);
		}
		return nextChain == null ? null : nextChain.isIgnore(entityClass,
				descriptor);
	}

	public String getName(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getName(entityClass, descriptor, this);
		}
		return nextChain == null ? null : nextChain.getName(entityClass,
				descriptor);
	}

	public Collection<String> getAliasNames(Class<?> entityClass,
			ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getAliasNames(entityClass, descriptor, this);
		}
		return nextChain == null ? null : nextChain.getAliasNames(entityClass,
				descriptor);
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

	public Boolean isPrimaryKey(Class<?> entityClass,
			ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isPrimaryKey(entityClass, descriptor, this);
		}
		return nextChain == null ? null : nextChain.isPrimaryKey(entityClass,
				descriptor);
	}

	public Boolean isNullable(Class<?> entityClass,
			ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isNullable(entityClass, descriptor, this);
		}
		return nextChain == null ? null : nextChain.isNullable(entityClass,
				descriptor);
	}

	public Boolean isEntity(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isEntity(entityClass, descriptor, this);
		}
		return nextChain == null ? null : nextChain.isEntity(entityClass,
				descriptor);
	}

	public Boolean isEntity(Class<?> entityClass) {
		if (iterator.hasNext()) {
			return iterator.next().isEntity(entityClass, this);
		}
		return nextChain == null ? null : nextChain.isEntity(entityClass);
	}

	public Boolean isVersionField(Class<?> entityClass,
			ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next()
					.isVersionField(entityClass, descriptor, this);
		}
		return nextChain == null ? null : nextChain.isVersionField(entityClass,
				descriptor);
	}

	@Override
	public Collection<Range<Double>> getNumberRanges(Class<?> entityClass,
			ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getNumberRanges(entityClass, descriptor,
					this);
		}
		return nextChain == null ? null : nextChain.getNumberRanges(
				entityClass, descriptor);
	}

	@Override
	public Boolean isAutoIncrement(Class<?> entityClass,
			ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isAutoIncrement(entityClass, descriptor,
					this);
		}
		return nextChain == null ? null : nextChain.isAutoIncrement(
				entityClass, descriptor);
	}

	@Override
	public String getComment(Class<?> entityClass) {
		if (iterator.hasNext()) {
			return iterator.next().getComment(entityClass, this);
		}
		return nextChain == null ? null : nextChain.getComment(entityClass);
	}

	@Override
	public String getComment(Class<?> entityClass,
			ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getComment(entityClass, descriptor, this);
		}
		return nextChain == null ? null : nextChain.getComment(entityClass,
				descriptor);
	}

	@Override
	public Boolean isIgnore(Class<?> entityClass) {
		if (iterator.hasNext()) {
			return iterator.next().isIgnore(entityClass, this);
		}
		return nextChain == null ? null : nextChain.isIgnore(entityClass);
	}

	@Override
	public String getCharsetName(Class<?> entityClass) {
		if (iterator.hasNext()) {
			return iterator.next().getCharsetName(entityClass, this);
		}
		return nextChain == null ? null : nextChain.getCharsetName(entityClass);
	}

	@Override
	public String getCharsetName(Class<?> entityClass,
			ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next()
					.getCharsetName(entityClass, descriptor, this);
		}
		return nextChain == null ? null : nextChain.getCharsetName(entityClass,
				descriptor);
	}

	@Override
	public Boolean isUnique(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isUnique(entityClass, descriptor, this);
		}
		return nextChain == null ? null : nextChain.isUnique(entityClass,
				descriptor);
	}

	@Override
	public Boolean isIncrement(Class<?> entityClass,
			ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isIncrement(entityClass, descriptor, this);
		}
		return nextChain == null ? null : nextChain.isIncrement(entityClass,
				descriptor);
	}

	@Override
	public Sort getSort(Class<?> entityClass, ParameterDescriptor descriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getSort(entityClass, descriptor, this);
		}
		return nextChain == null ? null : nextChain.getSort(entityClass,
				descriptor);
	}
}
