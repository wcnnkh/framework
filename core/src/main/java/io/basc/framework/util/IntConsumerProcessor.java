package io.basc.framework.util;

import java.util.Objects;

public interface IntConsumerProcessor<E extends Throwable> {
	void process(int source) throws E;

	/**
	 * Returns a composed {@code IntConsumer} that performs, in sequence, this
	 * operation followed by the {@code after} operation. If performing either
	 * operation throws an exception, it is relayed to the caller of the composed
	 * operation. If performing this operation throws an exception, the
	 * {@code after} operation will not be performed.
	 *
	 * @param after the operation to perform after this operation
	 * @return a composed {@code IntConsumer} that performs in sequence this
	 *         operation followed by the {@code after} operation
	 * @throws NullPointerException if {@code after} is null
	 */
	default IntConsumerProcessor<E> andThen(IntConsumerProcessor<? extends E> after) {
		Objects.requireNonNull(after);
		return (int t) -> {
			process(t);
			after.process(t);
		};
	}

	default Processor<Integer, Void, E> toProcessor() {
		return new Processor<Integer, Void, E>() {

			@Override
			public Void process(Integer source) throws E {
				IntConsumerProcessor.this.process(source);
				return null;
			}
		};
	}

	/**
	 * 在执行之前添加一个回调
	 * 
	 * @param processor
	 * @return
	 */
	default IntConsumerProcessor<E> beforeNesting(IntConsumerProcessor<E> processor) {
		if (processor == null) {
			return this;
		}

		return (int t) -> {
			try {
				process(t);
			} finally {
				processor.process(t);
			}
		};
	}

	/**
	 * 在回调之后追加一个回调
	 * 
	 * @param processor
	 * @return
	 */
	default IntConsumerProcessor<E> afterNesting(IntConsumerProcessor<E> processor) {
		if (processor == null) {
			return this;
		}

		return (int t) -> {
			try {
				processor.process(t);
			} finally {
				process(t);
			}
		};
	}
}
