package io.basc.framework.orm;

import io.basc.framework.data.domain.Range;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.util.Assert;

import java.util.Collection;
import java.util.Iterator;

public class ObjectRelationalResolverChain implements ObjectRelationalResolver {

	public static ObjectRelationalResolverChain build(
			Iterator<ObjectRelationalExtend> iterator) {
		return new ObjectRelationalResolverChain(iterator);
	}

	private final Iterator<ObjectRelationalExtend> iterator;
	private final ObjectRelationalResolver nextChain;

	public ObjectRelationalResolverChain(
			Iterator<ObjectRelationalExtend> iterator) {
		this(iterator, null);
	}

	public ObjectRelationalResolverChain(
			Iterator<ObjectRelationalExtend> iterator,
			@Nullable ObjectRelationalResolver nextChain) {
		Assert.requiredArgument(iterator != null, "iterator");
		this.iterator = iterator;
		this.nextChain = nextChain;
	}

	public Boolean ignore(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().ignore(entityClass, fieldDescriptor, this);
		}
		return nextChain == null ? null : nextChain.ignore(entityClass,
				fieldDescriptor);
	}

	public String getName(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getName(entityClass, fieldDescriptor, this);
		}
		return nextChain == null ? null : nextChain.getName(entityClass,
				fieldDescriptor);
	}

	public Collection<String> getAliasNames(Class<?> entityClass,
			FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getAliasNames(entityClass, fieldDescriptor,
					this);
		}
		return nextChain == null ? null : nextChain.getAliasNames(entityClass,
				fieldDescriptor);
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
			FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isPrimaryKey(entityClass, fieldDescriptor,
					this);
		}
		return nextChain == null ? null : nextChain.isPrimaryKey(entityClass,
				fieldDescriptor);
	}

	public Boolean isNullable(Class<?> entityClass,
			FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isNullable(entityClass, fieldDescriptor,
					this);
		}
		return nextChain == null ? null : nextChain.isNullable(entityClass,
				fieldDescriptor);
	}

	public Boolean isEntity(Class<?> entityClass,
			FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isEntity(entityClass, fieldDescriptor, this);
		}
		return nextChain == null ? null : nextChain.isEntity(entityClass,
				fieldDescriptor);
	}

	public Boolean isEntity(Class<?> entityClass) {
		if (iterator.hasNext()) {
			return iterator.next().isEntity(entityClass, this);
		}
		return nextChain == null ? null : nextChain.isEntity(entityClass);
	}

	public Boolean isVersionField(Class<?> entityClass,
			FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isVersionField(entityClass, fieldDescriptor,
					this);
		}
		return nextChain == null ? null : nextChain.isVersionField(entityClass,
				fieldDescriptor);
	}

	@Override
	public Range<Double> getNumberRange(Class<?> entityClass,
			FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getNumberRange(entityClass, fieldDescriptor, this);
		}
		return nextChain == null ? null : nextChain.getNumberRange(entityClass,
				fieldDescriptor);
	}
}
