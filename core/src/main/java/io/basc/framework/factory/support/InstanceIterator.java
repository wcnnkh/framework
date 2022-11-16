package io.basc.framework.factory.support;

import java.util.Iterator;
import java.util.NoSuchElementException;

import io.basc.framework.factory.InstanceFactory;

public class InstanceIterator<E> implements Iterator<E> {
	private final InstanceFactory instanceFactory;
	private final Iterator<String> iterator;
	private String name;

	public InstanceIterator(InstanceFactory instanceFactory, Iterator<String> iterator) {
		this.instanceFactory = instanceFactory;
		this.iterator = iterator;
	}

	public boolean hasNext() {
		while (name == null) {
			if (iterator == null || !iterator.hasNext()) {
				return false;
			}

			name = iterator.next();
			if (name != null && instanceFactory.isInstance(name)) {
				return true;
			} else {
				name = null;
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public E next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		E instance = (E) instanceFactory.getInstance(name);
		name = null;
		return instance;
	}

}
