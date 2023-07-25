package io.basc.framework.util.function;

import io.basc.framework.lang.Nullable;

public abstract class AbstractStreamOperations<T, E extends Throwable, C extends AbstractStreamOperations<T, E, C>>
		extends StandardCloser<T, E, C> implements StreamOperations<T, E> {

	public AbstractStreamOperations() {
		this(null, null);
	}

	public AbstractStreamOperations(@Nullable RunnableProcessor<? extends E> closeProcessor) {
		this(closeProcessor, null);
	}

	public AbstractStreamOperations(@Nullable ConsumeProcessor<? super T, ? extends E> closeHandler) {
		this(null, closeHandler);
	}

	public AbstractStreamOperations(@Nullable RunnableProcessor<? extends E> closeProcessor,
			@Nullable ConsumeProcessor<? super T, ? extends E> closeHandler) {
		super(closeProcessor, closeHandler);
	}
}
