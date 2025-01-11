package io.basc.framework.util.page;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;

import io.basc.framework.util.function.Functions;

public final class BrowseableIterator<E extends Browseable<?, ?>> implements Iterator<E> {
	private E pageables;
	private Supplier<E> current;
	private final Function<? super E, ? extends E> next;

	public BrowseableIterator(E pageables, Function<? super E, ? extends E> next) {
		this.pageables = pageables;
		this.current = Functions.toSupplier(pageables);
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
			try {
				return current.get();
			} finally {
				current = null;
			}
		} else {
			this.pageables = next.apply(this.pageables);
			return pageables;
		}
	}
}