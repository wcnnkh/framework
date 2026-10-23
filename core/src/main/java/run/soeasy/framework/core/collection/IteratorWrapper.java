package run.soeasy.framework.core.collection;

import java.util.Iterator;
import java.util.function.Consumer;

import run.soeasy.framework.core.domain.Wrapper;

@FunctionalInterface
public interface IteratorWrapper<E, W extends Iterator<E>> extends Iterator<E>, Wrapper<W> {

	@Override
	default boolean hasNext() {
		return getSource().hasNext();
	}

	@Override
	default E next() {
		return getSource().next();
	}

	@Override
	default void forEachRemaining(Consumer<? super E> action) {
		getSource().forEachRemaining(action);
	}

	@Override
	default void remove() {
		getSource().remove();
	}

}
