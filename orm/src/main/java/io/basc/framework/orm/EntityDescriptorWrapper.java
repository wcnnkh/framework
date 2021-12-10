package io.basc.framework.orm;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class EntityDescriptorWrapper<W extends EntityDescriptor<T>, T extends PropertyMetadata>
		extends EntityMetadataWrapper<W> implements EntityDescriptor<T> {

	public EntityDescriptorWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public List<T> getProperties() {
		return wrappedTarget.getProperties();
	}

	@Override
	public List<T> getPrimaryKeys() {
		return wrappedTarget.getPrimaryKeys();
	}

	@Override
	public List<T> getNotPrimaryKeys() {
		return wrappedTarget.getNotPrimaryKeys();
	}

	@Override
	public Stream<T> stream() {
		return wrappedTarget.stream();
	}

	@Override
	public Iterator<T> iterator() {
		return wrappedTarget.iterator();
	}

	@Override
	public Stream<T> columns() {
		return wrappedTarget.columns();
	}
}
