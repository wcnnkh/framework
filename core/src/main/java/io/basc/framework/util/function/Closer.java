package io.basc.framework.util.function;

import io.basc.framework.util.Endpoint;

public interface Closer<T, E extends Throwable> {
	void close(T source) throws E;

	Closer<T, E> onClose(Endpoint<? super T, ? extends E> closeHandler);
}
