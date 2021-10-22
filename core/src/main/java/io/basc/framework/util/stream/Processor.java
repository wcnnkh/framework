package io.basc.framework.util.stream;

import io.basc.framework.convert.Converter;
import io.basc.framework.lang.Nullable;

/**
 * 一个处理者的定义
 * 
 * @author shuchaowen
 *
 * @param <S> 数据来源
 * @param <T> 返回的结果
 * @param <E> 异常
 */
@FunctionalInterface
public interface Processor<S, T, E extends Throwable> {
	T process(S source) throws E;

	/**
	 * 在执行外部嵌套一个执行器
	 * 
	 * @param <B>
	 * @param processor
	 * @param closeProcessor
	 * @return
	 */
	default <B> Processor<B, T, E> beforeProcess(Processor<B, ? extends S, ? extends E> processor,
			@Nullable ConsumerProcessor<S, ? extends E> closeProcessor) {
		return new NestingProcessor<B, S, T, E>(processor, this, closeProcessor);
	}

	/**
	 * 在执行内部嵌套一个执行器
	 * 
	 * @param <A>
	 * @param processor
	 * @param closeProcessor
	 * @return
	 */
	default <A> Processor<S, A, E> afterProcess(Processor<T, ? extends A, ? extends E> processor,
			@Nullable ConsumerProcessor<T, ? extends E> closeProcessor) {
		return new NestingProcessor<S, T, A, E>(this, processor, closeProcessor);
	}

	default <X extends Throwable> Processor<S, T, X> exceptionConvert(Converter<Throwable, X> exceptionConverter) {
		return new Processor<S, T, X>() {

			@Override
			public T process(S source) throws X {
				try {
					return Processor.this.process(source);
				} catch (Throwable e) {
					throw exceptionConverter.convert(e);
				}
			}
		};
	}
}
