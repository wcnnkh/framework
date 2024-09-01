package io.basc.framework.util.function;

public interface Closeable<E extends Throwable> {
	void close() throws E;

	boolean isClosed();

	Closeable<E> onClose(RunnableProcessor<? extends E> closeHandler);
}
