package io.basc.framework.util.stream;

import io.basc.framework.lang.Nullable;

/**
 * 流式处理器<br/>
 * 默认不会自动关闭 {@link AbstractStreamProcessor#isAutoClose()}
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
			Processor<Throwable, TE, ? extends TE> exceptionConverter) {
		return new ExceptionConvertStreamProcessor<>(this, exceptionConverter);
	}
}
