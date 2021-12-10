package io.basc.framework.util.stream;

import java.util.function.Consumer;

/**
 * 一个回调的定义
 * 
 * @see Consumer
 * @author shuchaowen
 *
 * @param <S> 回调的数据类型
 * @param <E> 异常类型
 */
@FunctionalInterface
public interface ConsumerProcessor<S, E extends Throwable> {
	void process(S source) throws E;

	default Processor<S, Void, E> toProcessor() {
		return new Processor<S, Void, E>() {

			@Override
			public Void process(S source) throws E {
				ConsumerProcessor.this.process(source);
				return null;
			}
		};
	}

	/**
	 * 在执行之前添加一个回调
	 * 
	 * @param callback
	 * @return
	 */
	default ConsumerProcessor<S, E> beforeNesting(ConsumerProcessor<S, E> processor) {
		if (processor == null) {
			return this;
		}

		final ConsumerProcessor<S, E> self = this;
		return new ConsumerProcessor<S, E>() {

			@Override
			public void process(S source) throws E {
				try {
					processor.process(source);
				} finally {
					self.process(source);
				}
			}
		};
	}

	/**
	 * 在回调之后追加一个回调
	 * 
	 * @param callback
	 * @return
	 */
	default ConsumerProcessor<S, E> afterNesting(ConsumerProcessor<S, E> processor) {
		if (processor == null) {
			return this;
		}

		final ConsumerProcessor<S, E> self = this;
		return new ConsumerProcessor<S, E>() {

			@Override
			public void process(S source) throws E {
				try {
					self.process(source);
				} finally {
					processor.process(source);
				}
			}
		};
	}
}
