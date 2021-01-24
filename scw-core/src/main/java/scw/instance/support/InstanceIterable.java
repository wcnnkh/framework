package scw.instance.support;

import java.util.Iterator;

import scw.instance.factory.NoArgsInstanceFactory;

public final class InstanceIterable<E> implements Iterable<E> {
	private NoArgsInstanceFactory instanceFactory;
	private Iterable<String> names;

	public InstanceIterable(NoArgsInstanceFactory instanceFactory,
			Iterable<String> names) {
		this.instanceFactory = instanceFactory;
		this.names = names;
	}

	public Iterator<E> iterator() {
		return new InstanceIterator<E>(instanceFactory, names.iterator());
	}
}
