package io.basc.framework.util;

public interface StreamOperations<T, E extends Throwable> extends StreamSource<T, E> {
	@Override
	StreamOperations<T, E> onClose(ConsumeProcessor<? super T, ? extends E> closeHandler);

	void close() throws E;

	boolean isClosed();

	StreamOperations<T, E> onClose(RunnableProcessor<? extends E> closeHandler);

	@Override
	default <R> StreamOperations<R, E> stream(Processor<? super T, ? extends R, ? extends E> mapper) {
		StreamOperations<R, E> stream = of(() -> process(mapper));
		return stream.onClose(() -> close());
	}

	@SuppressWarnings("unchecked")
	public static <R, X extends Throwable> StreamOperations<R, X> of(Source<? extends R, ? extends X> source) {
		if (source instanceof StreamOperations) {
			return (StreamOperations<R, X>) source;
		}
		return new StandardStreamOperations<>(source);
	}

	@Override
	default <R, X extends Throwable> R process(Processor<? super T, ? extends R, ? extends X> processor) throws E, X {
		try {
			return StreamSource.super.process(processor);
		} finally {
			close();
		}
	}

	@Override
	default <X extends Throwable> void consume(ConsumeProcessor<? super T, ? extends X> processor) throws E, X {
		try {
			StreamSource.super.consume(processor);
		} finally {
			close();
		}
	}
}
