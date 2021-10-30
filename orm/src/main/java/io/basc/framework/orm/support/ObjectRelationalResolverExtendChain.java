package io.basc.framework.orm.support;

import io.basc.framework.data.domain.Range;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.orm.ObjectRelationalResolver;
import io.basc.framework.util.Assert;

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

	public Boolean isIgnore(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isIgnore(entityClass, fieldDescriptor, this);
		}
		return nextChain == null ? null : nextChain.isIgnore(entityClass, fieldDescriptor);
	}

	public String getName(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getName(entityClass, fieldDescriptor, this);
		}
		return nextChain == null ? null : nextChain.getName(entityClass, fieldDescriptor);
	}

	public Collection<String> getAliasNames(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getAliasNames(entityClass, fieldDescriptor, this);
		}
		return nextChain == null ? null : nextChain.getAliasNames(entityClass, fieldDescriptor);
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

	public Boolean isPrimaryKey(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isPrimaryKey(entityClass, fieldDescriptor, this);
		}
		return nextChain == null ? null : nextChain.isPrimaryKey(entityClass, fieldDescriptor);
	}

	public Boolean isNullable(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isNullable(entityClass, fieldDescriptor, this);
		}
		return nextChain == null ? null : nextChain.isNullable(entityClass, fieldDescriptor);
	}

	public Boolean isEntity(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isEntity(entityClass, fieldDescriptor, this);
		}
		return nextChain == null ? null : nextChain.isEntity(entityClass, fieldDescriptor);
	}

	public Boolean isEntity(Class<?> entityClass) {
		if (iterator.hasNext()) {
			return iterator.next().isEntity(entityClass, this);
		}
		return nextChain == null ? null : nextChain.isEntity(entityClass);
	}

	public Boolean isVersionField(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isVersionField(entityClass, fieldDescriptor, this);
		}
		return nextChain == null ? null : nextChain.isVersionField(entityClass, fieldDescriptor);
	}

	@Override
	public Collection<Range<Double>> getNumberRanges(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getNumberRanges(entityClass, fieldDescriptor, this);
		}
		return nextChain == null ? null : nextChain.getNumberRanges(entityClass, fieldDescriptor);
	}

	@Override
	public Boolean isAutoIncrement(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isAutoIncrement(entityClass, fieldDescriptor, this);
		}
		return nextChain == null ? null : nextChain.isAutoIncrement(entityClass, fieldDescriptor);
	}

	@Override
	public String getComment(Class<?> entityClass) {
		if (iterator.hasNext()) {
			return iterator.next().getComment(entityClass, this);
		}
		return nextChain == null ? null : nextChain.getComment(entityClass);
	}

	@Override
	public String getComment(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getComment(entityClass, fieldDescriptor, this);
		}
		return nextChain == null ? null : nextChain.getComment(entityClass, fieldDescriptor);
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
	public String getCharsetName(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().getCharsetName(entityClass, fieldDescriptor, this);
		}
		return nextChain == null ? null : nextChain.getCharsetName(entityClass, fieldDescriptor);
	}

	@Override
	public Boolean isUnique(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isUnique(entityClass, fieldDescriptor, this);
		}
		return nextChain == null ? null : nextChain.isUnique(entityClass, fieldDescriptor);
	}

	@Override
	public Boolean isIncrement(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		if (iterator.hasNext()) {
			return iterator.next().isIncrement(entityClass, fieldDescriptor, this);
		}
		return nextChain == null ? null : nextChain.isIncrement(entityClass, fieldDescriptor);
	}
}
