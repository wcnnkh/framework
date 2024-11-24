package io.basc.framework.util.function;

import lombok.NonNull;

public class StandardStreamSource<T, E extends Throwable, C extends StandardStreamSource<T, E, C>>
		extends AbstractStreamSource<T, E, C> {
	private final Source<? extends T, ? extends E> source;

	public StandardStreamSource(@NonNull Source<? extends T, ? extends E> source) {
		this(source, null);
	}

	public StandardStreamSource(@NonNull Source<? extends T, ? extends E> source,
			ConsumeProcessor<? super T, ? extends E> closeHandler) {
		super(closeHandler);
		this.source = source;
	}

	@Override
	public T get() throws E {
		return source.get();
	}
}