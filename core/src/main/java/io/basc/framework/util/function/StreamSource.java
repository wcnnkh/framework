package io.basc.framework.util.function;

import io.basc.framework.util.Closer;

public interface StreamSource<T, E extends Throwable> extends Source<T, E>, Closer<T, E> {
	T get() throws E;

	@Override
	StreamSource<T, E> onClose(ConsumeProcessor<? super T, ? extends E> closeHandler);

	default <R> StreamSource<R, E> stream(Processor<? super T, ? extends R, ? extends E> mapper) {
		return of(() -> process(mapper));
	}

	default <R, X extends Throwable> R process(Processor<? super T, ? extends R, ? extends X> processor) throws E, X {
		T source = get();
		try {
			return processor.process(source);
		} finally {
			close(source);
		}
	}

	default <X extends Throwable> void consume(ConsumeProcessor<? super T, ? extends X> processor) throws E, X {
		T source = get();
		try {
			processor.process(source);
		} finally {
			close(source);
		}
	}

	@SuppressWarnings("unchecked")
	public static <R, X extends Throwable> StreamSource<R, X> of(Source<? extends R, ? extends X> source) {
		if (source instanceof StreamSource) {
			return (StreamSource<R, X>) source;
		}
		return new StandardStreamSource<>(source);
	}
}
