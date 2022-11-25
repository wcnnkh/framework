package io.basc.framework.util;

import io.basc.framework.lang.Nullable;

public class StandardStreamSource<T, E extends Throwable, C extends StreamSource<T, E>>
		extends AbstractStreamSource<T, E, C> {
	private final Source<? extends T, ? extends E> source;

	public StandardStreamSource(Source<? extends T, ? extends E> source) {
		this(source, null);
	}

	public StandardStreamSource(Source<? extends T, ? extends E> source,
			@Nullable ConsumeProcessor<? super T, ? extends E> closeHandler) {
		super(closeHandler);
		this.source = source;
	}

	@Override
	public T get() throws E {
		return source.get();
	}
}