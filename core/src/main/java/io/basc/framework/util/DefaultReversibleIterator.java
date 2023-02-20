package io.basc.framework.util;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public final class DefaultReversibleIterator<E> implements ReversibleIterator<E> {
	private Iterator<? extends E> iterator;

	public DefaultReversibleIterator(List<? extends E> list) {
		Assert.requiredArgument(list != null, "list");
		this.iterator = list.listIterator();
	}

	public DefaultReversibleIterator(Iterator<? extends E> iterator) {
		Assert.requiredArgument(iterator != null, "iterator");
		this.iterator = iterator;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public E next() {
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

	@Override
	public boolean hasPrevious() {
		if (iterator instanceof ListIterator) {
			return ((ListIterator<? extends E>) iterator).hasPrevious();
		} else if (iterator instanceof ReversibleIterator) {
			return ((ReversibleIterator<? extends E>) iterator).hasPrevious();
		} else {
			List<E> list = toList();
			iterator = list.listIterator(list.size());
			return ((ListIterator<? extends E>) iterator).hasPrevious();
		}
	}

	@Override
	public E previous() {
		if (!hasPrevious()) {
			throw new NoSuchElementException(DefaultReverseIterator.class.getName() + "#previous");
		}

		if (iterator instanceof ListIterator) {
			return ((ListIterator<? extends E>) iterator).previous();
		} else if (iterator instanceof ReversibleIterator) {
			return ((ReversibleIterator<? extends E>) iterator).previous();
		}

		throw Assert.shouldNeverGetHere();
	}
}
