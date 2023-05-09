package io.basc.framework.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Setter;

@Setter
public class ConvertibleElements<S, E> extends SerializableElements<E> {
	private static final long serialVersionUID = 1L;
	private final transient Elements<S> source;
	private final transient Function<? super Stream<S>, ? extends Stream<E>> converter;

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
	protected ArrayList<E> create() {
		Stream<S> sourceStream = source.stream();
		try {
			Stream<E> targetStream = converter.apply(sourceStream);
			try {
				return targetStream.collect(Collectors.toCollection(ArrayList::new));
			} finally {
				targetStream.close();
			}
		} finally {
			sourceStream.close();
		}
	}

	@Override
	public Iterator<E> iterator() {
		if (source == null) {
			return super.iterator();
		}

		// 注意这里不可以直接调用Elements#stream方法，因为规定了iterator返回的是无需关闭的迭代
		Stream<S> sourceStream = Streams.stream(source.iterator());
		Stream<E> targetStream = converter.apply(sourceStream);
		return targetStream.iterator();
	}

	@Override
	public Spliterator<E> spliterator() {
		if (source == null) {
			return super.spliterator();
		}

		// 注意这里不可以直接调用Elements#stream方法，因为规定了iterator返回的是无需关闭的迭代
		Stream<S> sourceStream = Streams.stream(source.spliterator());
		Stream<E> targetStream = converter.apply(sourceStream);
		return targetStream.spliterator();
	}

	@Override
	public Stream<E> stream() {
		if (source != null) {
			return super.stream();
		}

		Stream<S> sourceStream = source.stream();
		return converter.apply(sourceStream);
	}
}
