package io.basc.framework.util.stream;

import io.basc.framework.convert.Converter;
import io.basc.framework.lang.Nullable;

import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 流处理器
 * 
 * @author shuchaowen
 * @param <T>
 * @param <E>
 * @see #close()
 * @see MapStreamProcessor
 * @see DefaultStreamProcessor
 * @see AutoCloseStreamProcessor
 * @see AutoCloseStreamProcessorWrapper
 */
public interface StreamProcessor<T, E extends Throwable> extends CallableProcessor<T, E> {
	@Nullable
	T process() throws E;

	<S> StreamProcessor<S, E> map(Processor<T, ? extends S, ? extends E> processor);

	StreamProcessor<T, E> onClose(CallbackProcessor<E> closeProcessor);

	void close() throws E;

	default <TE extends Throwable> StreamProcessor<T, TE> exceptionConvert(
			Converter<Throwable, TE> exceptionConverter) {
		return new ExceptionConvertStreamProcessor<>(this, exceptionConverter);
	}

	default <V> Stream<V> stream(Processor<T, Spliterator<V>, E> processor) throws E {
		T source = process();
		Spliterator<V> spliterator = processor.process(source);
		return StreamSupport.stream(spliterator, false).onClose(() -> {
			try {
				close();
			} catch (Throwable e) {
				if (e instanceof RuntimeException) {
					throw (RuntimeException) e;
				}
				throw new StreamException(e);
			}
		});
	}
}
