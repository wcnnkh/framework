package io.basc.framework.orm;

import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.util.Assert;

import java.util.Collection;
import java.util.Iterator;

public class ObjectRelationalMappingChain implements ObjectRelationalResolver {
	private final Iterator<ObjectRelationalExtend> iterator;
	private final ObjectRelationalResolver chain;

	public ObjectRelationalMappingChain(
			Iterator<ObjectRelationalExtend> iterator) {
		this(iterator, null);
	}

	public ObjectRelationalMappingChain(
			Iterator<ObjectRelationalExtend> iterator,
			@Nullable ObjectRelationalResolver chain) {
		Assert.requiredArgument(iterator != null, "iterator");
		this.iterator = iterator;
		this.chain = chain;
	}

	public Boolean ignore(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().ignore(entityClass, fieldDescriptor, this);
		}
		return chain == null ? null : chain
				.ignore(entityClass, fieldDescriptor);
	}

	public String getName(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getName(entityClass, fieldDescriptor, this);
		}
		return chain == null ? null : chain.getName(entityClass,
				fieldDescriptor);
	}

	public Collection<String> getAliasNames(Class<?> entityClass,
			FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getAliasNames(entityClass, fieldDescriptor,
					this);
		}
		return chain == null ? null : chain.getAliasNames(entityClass,
				fieldDescriptor);
	}

	public String getName(Class<?> entityClass) {
		if (iterator.hasNext()) {
			return iterator.next().getName(entityClass, this);
		}
		return chain == null ? null : chain.getName(entityClass);
	}

	public Collection<String> getAliasNames(Class<?> entityClass) {
		if (iterator.hasNext()) {
			return iterator.next().getAliasNames(entityClass, this);
		}
		return chain == null ? null : chain.getAliasNames(entityClass);
	}

	public Boolean isPrimaryKey(Class<?> entityClass,
			FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isPrimaryKey(entityClass, fieldDescriptor,
					this);
		}
		return chain == null ? null : chain.isPrimaryKey(entityClass,
				fieldDescriptor);
	}

	public Boolean isNullable(Class<?> entityClass,
			FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isNullable(entityClass, fieldDescriptor,
					chain);
		}
		return chain == null ? null : chain.isNullable(entityClass,
				fieldDescriptor);
	}

	public Boolean isEntity(Class<?> entityClass,
			FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isEntity(entityClass, fieldDescriptor, this);
		}
		return chain == null ? null : chain.isEntity(entityClass,
				fieldDescriptor);
	}

	public Boolean isEntity(Class<?> entityClass) {
		if (iterator.hasNext()) {
			return iterator.next().isEntity(entityClass, this);
		}
		return chain == null ? null : chain.isEntity(entityClass);
	}

	public Boolean isVersionField(Class<?> entityClass,
			FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isVersionField(entityClass, fieldDescriptor,
					this);
		}
		return chain == null ? null : chain.isVersionField(entityClass,
				fieldDescriptor);
	}

	public static ObjectRelationalMappingChain build(
			Iterator<ObjectRelationalExtend> iterator) {
		return new ObjectRelationalMappingChain(iterator);
	}
}
