package io.basc.framework.factory.support;

import java.util.Iterator;

import io.basc.framework.factory.InstanceFactory;

public final class InstanceIterable<E> implements Iterable<E> {
	private InstanceFactory instanceFactory;
	private Iterable<String> names;

	public InstanceIterable(InstanceFactory instanceFactory, Iterable<String> names) {
		this.instanceFactory = instanceFactory;
		this.names = names;
	}

	public Iterator<E> iterator() {
		return new InstanceIterator<E>(instanceFactory, names.iterator());
	}
}
