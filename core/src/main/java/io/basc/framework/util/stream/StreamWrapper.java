package io.basc.framework.util.stream;

import java.util.stream.Stream;

public final class StreamWrapper<T> extends StreamMapWrapper<T, StreamWrapper<T>> {

	public StreamWrapper(Stream<T> stream) {
		super(stream);
	}

	@Override
	protected StreamWrapper<T> wrap(Stream<T> stream) {
		if (stream instanceof StreamWrapper) {
			return (StreamWrapper<T>) stream;
		}
		return new StreamWrapper<>(stream);
	}
}
