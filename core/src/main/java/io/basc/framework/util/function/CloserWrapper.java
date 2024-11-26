package io.basc.framework.util.function;

import io.basc.framework.util.Endpoint;
import io.basc.framework.util.Wrapper;

@FunctionalInterface
public interface CloserWrapper<T, E extends Throwable, W extends Closer<T, E>> extends Closer<T, E>, Wrapper<W> {
	@Override
	default void close(T source) throws E {
		getSource().close(source);
	}

	@Override
	default Closer<T, E> onClose(Endpoint<? super T, ? extends E> closeHandler) {
		return getSource().onClose(closeHandler);
	}
}
