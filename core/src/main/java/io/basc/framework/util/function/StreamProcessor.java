package io.basc.framework.util.function;

import io.basc.framework.util.Endpoint;
import io.basc.framework.util.Pipeline;

public interface StreamProcessor<S, T, E extends Throwable> extends Pipeline<S, T, E>, Closer<T, E> {

	@Override
	default <V> StreamProcessor<S, V, E> andThen(Pipeline<? super T, ? extends V, ? extends E> after) {
		return of((source) -> {
			T s = process(source);
			try {
				return after.process(s);
			} finally {
				if (s != null && s != source) {
					close(s);
				}
			}
		});
	}

	@Override
	StreamProcessor<S, T, E> onClose(Endpoint<? super T, ? extends E> closeHandler);

	@SuppressWarnings("unchecked")
	public static <A, B, X extends Throwable> StreamProcessor<A, B, X> of(
			Pipeline<? super A, ? extends B, ? extends X> processor) {
		if (processor instanceof StreamProcessor) {
			return (StreamProcessor<A, B, X>) processor;
		}

		return new StandardStreamProcessor<>(processor);
	}
}
