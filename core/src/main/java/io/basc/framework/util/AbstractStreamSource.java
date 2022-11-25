package io.basc.framework.util;

import io.basc.framework.lang.Nullable;

public abstract class AbstractStreamSource<T, E extends Throwable, C extends StreamSource<T, E>>
		extends StandardCloser<T, E, C> implements StreamSource<T, E> {

	public AbstractStreamSource() {
		super(null);
	}

	public AbstractStreamSource(@Nullable ConsumeProcessor<? super T, ? extends E> closeHandler) {
		super(closeHandler);
	}
}
