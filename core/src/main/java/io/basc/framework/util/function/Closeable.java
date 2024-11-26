package io.basc.framework.util.function;

import io.basc.framework.util.Processor;

public interface Closeable<E extends Throwable> {
	void close() throws E;

	boolean isClosed();

	Closeable<E> onClose(Processor<? extends E> closeHandler);
}
