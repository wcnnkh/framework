package io.basc.framework.orm;

import io.basc.framework.mapper.Field;
import io.basc.framework.util.Wrapper;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class EntityStructureWrapper<M extends EntityStructure<T>, T extends Property> extends
		Wrapper<M> implements EntityStructure<T> {

	public EntityStructureWrapper(M wrappedTarget) {
		super(wrappedTarget);
	}
	
	@Override
	public String getName() {
		return wrappedTarget.getName();
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
	public List<T> getRows() {
		return wrappedTarget.getRows();
	}

	@Override
	public List<T> getNotPrimaryKeys() {
		return wrappedTarget.getNotPrimaryKeys();
	}

	@Override
	public List<T> getPrimaryKeys() {
		return wrappedTarget.getPrimaryKeys();
	}

	@Override
	public Stream<T> stream() {
		return wrappedTarget.stream();
	}

	@Override
	public Iterator<T> iterator() {
		return wrappedTarget.iterator();
	}
}
