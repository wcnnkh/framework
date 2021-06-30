package scw.util.stream;

import java.util.stream.Stream;

public final class AutoCloseStreamWrapper<T> extends AbstractAutoCloseStreamWrapper<T, AutoCloseStream<T>> {

	public AutoCloseStreamWrapper(Stream<T> stream) {
		super(stream);
	}

	@Override
	protected AutoCloseStream<T> wrapper(Stream<T> stream) {
		if (stream instanceof AutoCloseStream) {
			return (AutoCloseStream<T>) stream;
		}
		return new AutoCloseStreamWrapper<>(stream);
	}
}
