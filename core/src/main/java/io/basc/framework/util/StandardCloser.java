package io.basc.framework.util;

public class StandardCloser<T, E extends Throwable, C extends Closer<T, E>> implements Closer<T, E> {
	private ConsumeProcessor<? super T, ? extends E> closeHandler;

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
