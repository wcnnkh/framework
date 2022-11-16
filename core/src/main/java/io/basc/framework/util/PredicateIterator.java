package io.basc.framework.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import io.basc.framework.lang.Nullable;

public class PredicateIterator<E> extends ConvertibleIterator<E, E> {
	private final Predicate<? super E> predicate;
	private Supplier<E> supplier;

	public PredicateIterator(Iterator<? extends E> iterator, @Nullable Predicate<? super E> predicate) {
		super(iterator, Function.identity());
		this.predicate = predicate;
	}

	@Override
	public boolean hasNext() {
		if (predicate == null) {
			return super.hasNext();
		}

		if (supplier != null) {
			return true;
		}

		if (super.hasNext()) {
			E e = super.next();
			if (predicate.test(e)) {
				supplier = () -> e;
				return true;
			}
		}
		return false;
	}

	@Override
	public E next() {
		if (predicate == null) {
			return super.next();
		}

		if (hasNext()) {
			try {
				return supplier.get();
			} finally {
				supplier = null;
			}
		}
		throw new NoSuchElementException(getClass().getName());
	}

	@Override
	public void remove() {
		if (predicate == null) {
			super.remove();
			return;
		}

		if (hasNext()) {
			supplier = null;
		}
	}

	@Override
	public void forEachRemaining(Consumer<? super E> action) {
		if (predicate == null) {
			super.forEachRemaining(action);
			return;
		}

		super.forEachRemaining((e) -> {
			if (predicate.test(e)) {
				return;
			}
			action.accept(e);
		});
	}

}
