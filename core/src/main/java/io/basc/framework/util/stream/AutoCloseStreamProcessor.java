package io.basc.framework.util.stream;

import io.basc.framework.convert.Converter;
import io.basc.framework.lang.Nullable;

import java.util.Spliterator;

/**
 * 一个执行后会自动关闭的流处理器
 * 
 * @author shuchaowen
 *
 * @param <T>
 * @param <E>
 * @see AutoCloseStreamProcessorWrapper
 */
public interface AutoCloseStreamProcessor<T, E extends Throwable> extends StreamProcessor<T, E> {
	/**
	 * 在执行此方法后会自动调用close方法
	 */
	@Nullable
	T process() throws E;

	<S> AutoCloseStreamProcessor<S, E> map(Processor<T, ? extends S, ? extends E> processor);

	AutoCloseStreamProcessor<T, E> onClose(RunnableProcessor<E> closeProcessor);

	boolean isClosed();
	
	@Override
	default <TE extends Throwable> AutoCloseStreamProcessor<T, TE> exceptionConvert(
			Converter<Throwable, TE> exceptionConverter) {
		return new AutoCloseStreamProcessorWrapper<T, TE>(StreamProcessor.super.exceptionConvert(exceptionConverter));
	}

	@Override
	default <V> StreamWrapper<V> stream(Processor<T, Spliterator<V>, E> processor) throws E {
		return new StreamWrapper<V>(StreamProcessor.super.stream(processor));
	}

	@Override
	default void process(ConsumerProcessor<T, ? extends E> callback) throws E {
		try {
			StreamProcessor.super.process(callback);
		} finally {
			close();
		}
	}

	@Override
	default <V> V process(Processor<T, ? extends V, ? extends E> processor) throws E {
		try {
			return StreamProcessor.super.process(processor);
		} finally {
			close();
		}
	}
}
