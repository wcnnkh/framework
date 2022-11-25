package io.basc.framework.util;

import java.util.concurrent.atomic.AtomicBoolean;

import io.basc.framework.lang.Nullable;

public abstract class AbstractStreamOperations<T, E extends Throwable, C extends StreamOperations<T, E>>
		extends AbstractStreamSource<T, E, C> implements StreamOperations<T, E> {
	private final AtomicBoolean closed = new AtomicBoolean(false);
	private RunnableProcessor<? extends E> closeHandler;

	public AbstractStreamOperations() {
		this(null);
	}

	public AbstractStreamOperations(@Nullable RunnableProcessor<? extends E> closeHandler) {
		this(null, closeHandler);
	}

	public AbstractStreamOperations(@Nullable ConsumeProcessor<? super T, ? extends E> closeProcessor,
			@Nullable RunnableProcessor<? extends E> closeHandler) {
		super(closeProcessor);
		this.closeHandler = closeHandler;
	}

	@Override
	public void close() throws E {
		if (closed.compareAndSet(false, true) && closeHandler != null) {
			closeHandler.process();
		}
	}

	@Override
	public boolean isClosed() {
		return closed.get();
	}

	@SuppressWarnings("unchecked")
	@Override
	public C onClose(RunnableProcessor<? extends E> closeHandler) {
		if (closeHandler == null) {
			return (C) this;
		}

		if (this.closeHandler == null) {
			this.closeHandler = closeHandler;
		} else {
			this.closeHandler = XUtils.composeWithExceptions(this.closeHandler, closeHandler);
		}
		return (C) this;
	}

}
