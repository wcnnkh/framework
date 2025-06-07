package run.soeasy.framework.core.collection;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class ConvertibleIterator<S, E> implements Iterator<E> {
	@NonNull
	private final Iterator<? extends S> iterator;
	@NonNull
	private final Function<? super S, ? extends E> converter;

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public E next() {
		S s = iterator.next();
		return converter.apply(s);
	}

	@Override
	public void remove() {
		iterator.remove();
	}

	@Override
	public void forEachRemaining(@NonNull Consumer<? super E> action) {
		iterator.forEachRemaining((s) -> {
			E e = converter.apply(s);
			action.accept(e);
		});
	}
}
