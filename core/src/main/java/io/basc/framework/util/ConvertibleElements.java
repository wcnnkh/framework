package io.basc.framework.util;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.Data;

@Data
public class ConvertibleElements<S, E> implements Elements<E> {
	private final Elements<S> source;
	private final Function<? super Stream<S>, ? extends Stream<E>> converter;

	public ConvertibleElements(Elements<S> source, Function<? super Stream<S>, ? extends Stream<E>> converter) {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(converter != null, "converter");
		this.source = source;
		this.converter = converter;
	}

	public Function<? super Stream<S>, ? extends Stream<E>> getConverter() {
		return converter;
	}

	@Override
	public Iterator<E> iterator() {
		// 注意这里不可以直接调用Elements#stream方法，因为规定了iterator返回的是无需关闭的迭代
		Stream<S> sourceStream = Streams.stream(source.iterator());
		Stream<E> targetStream = converter.apply(sourceStream);
		return targetStream.iterator();
	}

	@Override
	public Spliterator<E> spliterator() {
		// 注意这里不可以直接调用Elements#stream方法，因为规定了iterator返回的是无需关闭的迭代
		Stream<S> sourceStream = Streams.stream(source.spliterator());
		Stream<E> targetStream = converter.apply(sourceStream);
		return targetStream.spliterator();
	}

	@Override
	public Stream<E> stream() {
		Stream<S> sourceStream = source.stream();
		return converter.apply(sourceStream);
	}
}
