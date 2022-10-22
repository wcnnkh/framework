package io.basc.framework.util.stream;

import java.util.Objects;

public interface DoubleConsumerProcessor<E extends Throwable> {
	void process(double source) throws E;

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
	default DoubleConsumerProcessor<E> andThen(DoubleConsumerProcessor<? extends E> after) {
		Objects.requireNonNull(after);
		return (double t) -> {
			process(t);
			after.process(t);
		};
	}

	default Processor<Double, Void, E> toProcessor() {
		return new Processor<Double, Void, E>() {

			@Override
			public Void process(Double source) throws E {
				DoubleConsumerProcessor.this.process(source);
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
	default DoubleConsumerProcessor<E> beforeNesting(DoubleConsumerProcessor<E> processor) {
		if (processor == null) {
			return this;
		}

		return (double t) -> {
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
	 * @param callback
	 * @return
	 */
	default DoubleConsumerProcessor<E> afterNesting(DoubleConsumerProcessor<E> processor) {
		if (processor == null) {
			return this;
		}

		return (double t) -> {
			try {
				processor.process(t);
			} finally {
				process(t);
			}
		};
	}
}
