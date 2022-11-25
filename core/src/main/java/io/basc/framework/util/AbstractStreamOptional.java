package io.basc.framework.util;

import io.basc.framework.lang.Nullable;

public abstract class AbstractStreamOptional<T, C extends StreamOptional<T>>
		extends AbstractStreamOperations<T, RuntimeException, C> implements StreamOptional<T> {

	public AbstractStreamOptional() {
		this(null);
	}

	public AbstractStreamOptional(@Nullable RunnableProcessor<? extends RuntimeException> closeHandler) {
		this(null, closeHandler);
	}

	public AbstractStreamOptional(@Nullable ConsumeProcessor<? super T, ? extends RuntimeException> closeProcessor,
			@Nullable RunnableProcessor<? extends RuntimeException> closeHandler) {
		super(closeProcessor, closeHandler);
	}
}
