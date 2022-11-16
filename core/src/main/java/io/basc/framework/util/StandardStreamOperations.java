package io.basc.framework.util;

public class StandardStreamOperations<T, E extends Throwable, C extends StreamOperations<T, E>>
		extends AbstractStreamOperations<T, E, C> {
	private final Source<? extends T, ? extends E> source;

	public StandardStreamOperations(Source<? extends T, ? extends E> source) {
		this.source = source;
	}

	@Override
	public T get() throws E {
		return source.get();
	}
}
