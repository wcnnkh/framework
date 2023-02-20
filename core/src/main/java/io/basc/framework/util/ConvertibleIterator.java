package io.basc.framework.util;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConvertibleIterator<S, E> implements Iterator<E> {
	private final Iterator<? extends S> iterator;
	private final Function<? super S, ? extends E> function;

	public ConvertibleIterator(Iterator<? extends S> iterator, Function<? super S, ? extends E> function) {
		Assert.requiredArgument(iterator != null, "iterator");
		Assert.requiredArgument(function != null, "function");
		this.iterator = iterator;
		this.function = function;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public E next() {
		return function.apply(iterator.next());
	}

	@Override
	public void remove() {
		iterator.remove();
	}

	@Override
	public void forEachRemaining(Consumer<? super E> action) {
		iterator.forEachRemaining((e) -> action.accept(function.apply(e)));
	}
}
