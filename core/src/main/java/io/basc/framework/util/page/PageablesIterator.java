package io.basc.framework.util.page;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;

import io.basc.framework.util.StaticSupplier;

public final class PageablesIterator<E extends Pageables<?, ?>> implements Iterator<E> {
	private E pageables;
	private Supplier<E> current;
	private final Function<? super E, ? extends E> next;

	public PageablesIterator(E pageables, Function<? super E, ? extends E> next) {
		this.pageables = pageables;
		this.current = new StaticSupplier<E>(pageables);
		this.next = next;
	}

	@Override
	public boolean hasNext() {
		if (current != null) {
			return true;
		}

		return pageables.hasNext();
	}

	@Override
	public E next() {
		if (current != null) {
			E value = current.get();
			current = null;
			return value;
		} else {
			this.pageables = next.apply(this.pageables);
			return pageables;
		}
	}
}
