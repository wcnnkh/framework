package run.soeasy.framework.core.page;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;

public final class BrowseableIterator<E extends Browseable<?, ?>> implements Iterator<E> {
	private E pageables;
	private Supplier<E> current;
	private final Function<? super E, ? extends E> next;

	public BrowseableIterator(E pageables, Function<? super E, ? extends E> next) {
		this.pageables = pageables;
		this.current = () -> pageables;
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