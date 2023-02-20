package io.basc.framework.util;

import java.util.function.Function;
import java.util.function.Predicate;

public interface StreamOptional<T> extends Optional<T>, AutoCloseable, StreamOperations<T, RuntimeException> {
	@Override
	void close();

	boolean isClosed();

	@Override
	<U> StreamOptional<U> convert(Function<? super T, ? extends U> converter);

	@Override
	default <U> StreamOptional<U> map(Function<? super T, ? extends U> mapper) {
		return convert((e) -> e == null ? null : mapper.apply(e));
	}

	@Override
	default StreamOptional<T> filter(Predicate<? super T> predicate) {
		return convert((e) -> (e != null && predicate.test(e)) ? e : null);
	}

	@Override
	StreamOptional<T> onClose(ConsumeProcessor<? super T, ? extends RuntimeException> closeHandler);

	@Override
	StreamOptional<T> onClose(RunnableProcessor<? extends RuntimeException> closeHandler);

	@Override
	default <R> StreamOptional<R> stream(Processor<? super T, ? extends R, ? extends RuntimeException> mapper) {
		StreamOptional<R> stream = of(() -> process(mapper));
		return stream.onClose(() -> close());
	}

	@SuppressWarnings("unchecked")
	public static <R> StreamOptional<R> of(Source<? extends R, ? extends RuntimeException> source) {
		if (source instanceof StreamOptional) {
			return (StreamOptional<R>) source;
		}
		return new StandardStreamOptional<>(source);
	}
}
