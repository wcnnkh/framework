package io.basc.framework.util.function;

import io.basc.framework.lang.Nullable;

public class StandardCloser<T, E extends Throwable, C extends StandardCloser<T, E, C>> extends StandardCloseable<E, C>
		implements Closer<T, E> {
	private ConsumeProcessor<? super T, ? extends E> closeHandler;

	public StandardCloser() {
		this(null, null);
	}

	public StandardCloser(@Nullable RunnableProcessor<? extends E> closeProcessor) {
		this(closeProcessor, null);
	}

	public StandardCloser(@Nullable ConsumeProcessor<? super T, ? extends E> closeHandler) {
		this(null, closeHandler);
	}

	public StandardCloser(@Nullable RunnableProcessor<? extends E> closeProcessor,
			@Nullable ConsumeProcessor<? super T, ? extends E> closeHandler) {
		super(closeProcessor);
		this.closeHandler = closeHandler;
	}

	@Override
	public void close(T source) throws E {
		if (source != null && this.closeHandler != null) {
			closeHandler.process(source);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public C onClose(ConsumeProcessor<? super T, ? extends E> closeHandler) {
		if (closeHandler == null) {
			return (C) this;
		}

		if (this.closeHandler == null) {
			this.closeHandler = closeHandler;
		} else {
			ConsumeProcessor<? super T, ? extends E> old = this.closeHandler;
			this.closeHandler = (t) -> {
				try {
					old.process(t);
				} finally {
					closeHandler.process(t);
				}
			};
		}
		return (C) this;
	}
}
