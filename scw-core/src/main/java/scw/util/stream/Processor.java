package scw.util.stream;

import scw.lang.Nullable;

/**
 * 一个处理者的定义
 * 
 * @author shuchaowen
 *
 * @param <S>
 *            数据来源
 * @param <T>
 *            返回的结果
 * @param <E>
 *            异常
 */
@FunctionalInterface
public interface Processor<S, T, E extends Throwable> {
	T process(S source) throws E;

	/**
	 * 在执行外部嵌套一个执行器
	 * @param <B>
	 * @param processor
	 * @param closeProcessor
	 * @return
	 */
	default <B> Processor<B, T, E> beforeProcess(Processor<B, ? extends S, E> processor,
			@Nullable Callback<S, E> closeProcessor) {
		return new NestingProcessor<B, S, T, E>(processor, this, closeProcessor);
	}

	/**
	 * 在执行内部嵌套一个执行器
	 * @param <A>
	 * @param processor
	 * @param closeProcessor
	 * @return
	 */
	default <A> Processor<S, A, E> afterProcess(Processor<T, ? extends A, E> processor,
			@Nullable Callback<T, E> closeProcessor) {
		return new NestingProcessor<S, T, A, E>(this, processor, closeProcessor);
	}
}
