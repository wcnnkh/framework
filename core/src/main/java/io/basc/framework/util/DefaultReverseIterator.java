package io.basc.framework.util;

import java.util.Iterator;
import java.util.function.Consumer;

final class DefaultReverseIterator<E> implements ReverseIterator<E> {
	private final Iterator<? extends E> iterator;

	public DefaultReverseIterator(Iterator<? extends E> iterator) {
		Assert.requiredArgument(iterator != null, "iterator");
		this.iterator = iterator;
	}

	@Override
	public boolean hasPrevious() {
		return iterator.hasNext();
	}

	@Override
	public E previous() {
		return iterator.next();
	}

	@Override
	public void remove() {
		iterator.remove();
	}

	@Override
	public void forEachRemaining(Consumer<? super E> action) {
		iterator.forEachRemaining(action);
	}
}
