package run.soeasy.framework.core.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import lombok.NonNull;

public final class LinkedIterator<E> implements Iterator<E> {
	private E last;
	private Supplier<E> currentSupplier;
	private final Predicate<? super E> hasNext;
	private final Function<? super E, ? extends E> next;

	public LinkedIterator(@NonNull E current, @NonNull Predicate<? super E> hasNext,
			@NonNull Function<? super E, ? extends E> next) {
		this.last = current;
		this.currentSupplier = () -> current;
		this.hasNext = hasNext;
		this.next = next;
	}

	@Override
	public synchronized boolean hasNext() {
		if (currentSupplier == null && last != null && hasNext.test(last)) {
			E next = this.next.apply(this.last);
			this.currentSupplier = () -> next;
		}
		return currentSupplier != null;
	}

	@Override
	public E next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		try {
			return this.last = currentSupplier.get();
		} finally {
			this.currentSupplier = null;
		}
	}
}