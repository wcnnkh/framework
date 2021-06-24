package scw.util.stream;

import scw.lang.Nullable;

/**
 * 一个执行后会自动关闭的流处理器
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

	<S> AutoCloseStreamProcessor<S, E> map(Processor<T, S, E> processor);

	AutoCloseStreamProcessor<T, E> onClose(CallbackProcessor<E> closeProcessor);
}
