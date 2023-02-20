package io.basc.framework.util;

import io.basc.framework.lang.Nullable;

public abstract class AbstractStreamOptional<T, C extends AbstractStreamOptional<T, C>>
		extends AbstractStreamOperations<T, RuntimeException, C> implements StreamOptional<T> {

	public AbstractStreamOptional() {
		this(null, null);
	}

	public AbstractStreamOptional(@Nullable RunnableProcessor<? extends RuntimeException> closeProcessor) {
		this(closeProcessor, null);
	}

	public AbstractStreamOptional(@Nullable ConsumeProcessor<? super T, ? extends RuntimeException> closeHandler) {
		this(null, closeHandler);
	}

	public AbstractStreamOptional(@Nullable RunnableProcessor<? extends RuntimeException> closeProcessor,
			@Nullable ConsumeProcessor<? super T, ? extends RuntimeException> closeHandler) {
		super(closeProcessor, closeHandler);
	}
}
