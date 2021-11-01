package io.basc.framework.util.stream;

import io.basc.framework.convert.Converter;
import io.basc.framework.lang.Nullable;

/**
 * 流式处理器
 * 
 * @author shuchaowen
 * @param <T>
 * @param <E>
 * @see #close()
 * @see DefaultStreamProcessor
 */
public interface StreamProcessor<T, E extends Throwable> extends CallableProcessor<T, E> {
	@Nullable
	T process() throws E;
	
	<S> StreamProcessor<S, E> map(Processor<T, ? extends S, ? extends E> processor);

	StreamProcessor<T, E> onClose(RunnableProcessor<E> closeProcessor);

	boolean isAutoClose();

	void setAutoClose(boolean autoClose);

	void close() throws E;

	boolean isClosed();

	default <TE extends Throwable> StreamProcessor<T, TE> exceptionConvert(
			Converter<Throwable, TE> exceptionConverter) {
		return new ExceptionConvertStreamProcessor<>(this, exceptionConverter);
	}
}
