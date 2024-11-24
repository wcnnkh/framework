package io.basc.framework.util.function;

public abstract class AbstractStreamSource<T, E extends Throwable, C extends AbstractStreamSource<T, E, C>>
		extends StandardCloser<T, E, C> implements StreamSource<T, E> {

	public AbstractStreamSource() {
		this(null, null);
	}

	public AbstractStreamSource(RunnableProcessor<? extends E> closeProcessor) {
		this(closeProcessor, null);
	}

	public AbstractStreamSource(ConsumeProcessor<? super T, ? extends E> closeHandler) {
		this(null, closeHandler);
	}

	public AbstractStreamSource(RunnableProcessor<? extends E> closeProcessor,
			ConsumeProcessor<? super T, ? extends E> closeHandler) {
		super(closeProcessor, closeHandler);
	}
}
