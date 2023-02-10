package io.basc.framework.util;

import io.basc.framework.lang.Nullable;

public abstract class AbstractCursor<E, C extends AbstractCursor<E, C>> extends StandardCloseable<RuntimeException, C>
		implements Cursor<E> {

	public AbstractCursor() {
		this(null);
	}

	public AbstractCursor(@Nullable RunnableProcessor<? extends RuntimeException> closeProcessor) {
		super(closeProcessor);
	}
}
