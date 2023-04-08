package io.basc.framework.util;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class StreamElements<E> extends StreamableWrapper<E, Streamable<E>> implements Elements<E> {

	public StreamElements(Streamable<E> streamable) {
		super(streamable);
	}

	@Override
	public <U> Elements<U> convert(Function<? super Stream<E>, ? extends Stream<U>> converter) {
		return Elements.super.convert(converter);
	}

	@Override
	public Elements<E> filter(Predicate<? super E> predicate) {
		return Elements.super.filter(predicate);
	}

	@Override
	public <U> Elements<U> map(Function<? super E, ? extends U> mapper) {
		return Elements.super.map(mapper);
	}

	@Override
	public <U> Elements<U> flatMap(Function<? super E, ? extends Streamable<U>> mapper) {
		return Elements.super.flatMap(mapper);
	}

	@Override
	public Iterator<E> iterator() {
		return wrappedTarget.toList().iterator();
	}
}
