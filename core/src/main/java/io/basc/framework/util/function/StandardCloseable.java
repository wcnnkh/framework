package io.basc.framework.util.function;

import java.util.concurrent.atomic.AtomicBoolean;

import io.basc.framework.util.XUtils;

public class StandardCloseable<E extends Throwable, C extends Closeable<E>> implements Closeable<E> {
	private final AtomicBoolean closed = new AtomicBoolean(false);
	private RunnableProcessor<? extends E> closeHandler;

	public StandardCloseable() {
		this(null);
	}

	public StandardCloseable(RunnableProcessor<? extends E> closeHandler) {
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
