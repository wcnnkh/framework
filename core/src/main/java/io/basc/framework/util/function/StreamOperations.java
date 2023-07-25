package io.basc.framework.util.function;

import io.basc.framework.util.Closeable;

public interface StreamOperations<T, E extends Throwable> extends StreamSource<T, E>, Closeable<E> {
	@SuppressWarnings("unchecked")
	public static <R, X extends Throwable> StreamOperations<R, X> of(Source<? extends R, ? extends X> source) {
		if (source instanceof StreamOperations) {
			return (StreamOperations<R, X>) source;
		}
		return new StandardStreamOperations<>(source);
	}

	@Override
	default <X extends Throwable> void consume(ConsumeProcessor<? super T, ? extends X> processor) throws E, X {
		try {
			StreamSource.super.consume(processor);
		} finally {
			close();
		}
	}

	@Override
	StreamOperations<T, E> onClose(ConsumeProcessor<? super T, ? extends E> closeHandler);

	StreamOperations<T, E> onClose(RunnableProcessor<? extends E> closeHandler);

	@Override
	default <R, X extends Throwable> R process(Processor<? super T, ? extends R, ? extends X> processor) throws E, X {
		try {
			return StreamSource.super.process(processor);
		} finally {
			close();
		}
	}

	@Override
	default <R> StreamOperations<R, E> stream(Processor<? super T, ? extends R, ? extends E> mapper) {
		return new StandardStreamOperations<>(this, mapper, null, null);
	}
}
