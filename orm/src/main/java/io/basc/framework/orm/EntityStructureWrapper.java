package io.basc.framework.orm;

import java.util.Collection;

import io.basc.framework.mapper.Field;

public class EntityStructureWrapper<W extends EntityStructure<T>, T extends Property>
		extends EntityDescriptorWrapper<W, T> implements EntityStructure<T> {

	public EntityStructureWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public Class<?> getEntityClass() {
		return wrappedTarget.getEntityClass();
	}

	@Override
	public T find(Field field) {
		return wrappedTarget.find(field);
	}

	@Override
	public EntityStructure<T> rename(String name) {
		return wrappedTarget.rename(name);
	}

	@Override
	public Collection<String> getAliasNames() {
		return wrappedTarget.getAliasNames();
	}
}
