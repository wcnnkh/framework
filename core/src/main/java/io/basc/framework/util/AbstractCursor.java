package io.basc.framework.util;

import io.basc.framework.lang.Nullable;

public abstract class AbstractCursor<E, C extends Cursor<E>> extends AbstractStreamOptional<E, C> implements Cursor<E> {

	public AbstractCursor() {
		this(null);
	}

	public AbstractCursor(@Nullable RunnableProcessor<? extends RuntimeException> closeHandler) {
		this(null, closeHandler);
	}

	public AbstractCursor(@Nullable ConsumeProcessor<? super E, ? extends RuntimeException> closeProcessor,
			@Nullable RunnableProcessor<? extends RuntimeException> closeHandler) {
		super(closeProcessor, closeHandler);
	}
}
