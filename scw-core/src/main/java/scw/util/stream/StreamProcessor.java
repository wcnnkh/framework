package scw.util.stream;

import scw.lang.Nullable;

/**
 * 流处理器
 * 
 * @author shuchaowen
 * @param <T>
 * @param <E>
 * @see #close()
 * @see MapStreamProcessor
 * @see DefaultStreamProcessor
 */
public interface StreamProcessor<T, E extends Throwable> extends CallableProcessor<T, E> {
	/**
	 * 在执行此方法后会自动调用close方法
	 */
	@Nullable
	T process() throws E;

	<S> StreamProcessor<S, E> map(Processor<T, S, E> processor);

	StreamProcessor<T, E> onClose(CallbackProcessor<E> closeProcessor);

	void close() throws E;
}
