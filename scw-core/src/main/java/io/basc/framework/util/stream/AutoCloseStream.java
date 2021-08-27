package io.basc.framework.util.stream;

import java.util.stream.Stream;

public final class AutoCloseStream<T> extends AbstractAutoCloseStream<T, AutoCloseStream<T>> {

	public AutoCloseStream(Stream<T> stream) {
		super(stream);
	}

	@Override
	protected AutoCloseStream<T> wrapper(Stream<T> stream) {
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<T>) stream;
		}
		return new AutoCloseStream<>(stream);
	}
}
