package io.basc.framework.util;

public interface Closer<T, E extends Throwable> {
	void close(T source) throws E;

	Closer<T, E> onClose(ConsumeProcessor<? super T, ? extends E> closeHandler);
}
