package io.basc.framework.util;

import io.basc.framework.lang.Nullable;

public abstract class AbstractStreamSource<T, E extends Throwable, C extends AbstractStreamSource<T, E, C>>
		extends StandardCloser<T, E, C> implements StreamSource<T, E> {

	public AbstractStreamSource() {
		this(null, null);
	}

	public AbstractStreamSource(@Nullable RunnableProcessor<? extends E> closeProcessor) {
		this(closeProcessor, null);
	}

	public AbstractStreamSource(@Nullable ConsumeProcessor<? super T, ? extends E> closeHandler) {
		this(null, closeHandler);
	}

	public AbstractStreamSource(@Nullable RunnableProcessor<? extends E> closeProcessor,
			@Nullable ConsumeProcessor<? super T, ? extends E> closeHandler) {
		super(closeProcessor, closeHandler );
	}
}
