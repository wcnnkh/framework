package io.basc.framework.util.function;

public abstract class AbstractStreamOperations<T, E extends Throwable, C extends AbstractStreamOperations<T, E, C>>
		extends StandardCloser<T, E, C> implements StreamOperations<T, E> {

	public AbstractStreamOperations() {
		this(null, null);
	}

	public AbstractStreamOperations(RunnableProcessor<? extends E> closeProcessor) {
		this(closeProcessor, null);
	}

	public AbstractStreamOperations(ConsumeProcessor<? super T, ? extends E> closeHandler) {
		this(null, closeHandler);
	}

	public AbstractStreamOperations(RunnableProcessor<? extends E> closeProcessor,
			ConsumeProcessor<? super T, ? extends E> closeHandler) {
		super(closeProcessor, closeHandler);
	}
}
