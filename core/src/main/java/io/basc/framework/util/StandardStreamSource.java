package io.basc.framework.util;

public class StandardStreamSource<T, E extends Throwable, C extends StreamSource<T, E>>
		extends AbstractStreamSource<T, E, C> {
	private final Source<? extends T, ? extends E> source;

	public StandardStreamSource(Source<? extends T, ? extends E> source) {
		this.source = source;
	}

	@Override
	public T get() throws E {
		return source.get();
	}
}
