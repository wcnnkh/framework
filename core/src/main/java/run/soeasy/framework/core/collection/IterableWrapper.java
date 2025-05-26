package run.soeasy.framework.core.collection;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

import run.soeasy.framework.core.domain.Wrapper;

public interface IterableWrapper<E, W extends Iterable<E>> extends Iterable<E>, Wrapper<W> {
	@Override
	default Iterator<E> iterator() {
		return getSource().iterator();
	}

	@Override
	default void forEach(Consumer<? super E> action) {
		getSource().forEach(action);
	}

	@Override
	default Spliterator<E> spliterator() {
		return getSource().spliterator();
	}
}
