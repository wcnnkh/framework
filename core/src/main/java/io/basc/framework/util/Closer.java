package io.basc.framework.util;

import io.basc.framework.util.function.ConsumeProcessor;

public interface Closer<T, E extends Throwable> {
	void close(T source) throws E;

	Closer<T, E> onClose(ConsumeProcessor<? super T, ? extends E> closeHandler);
}
