package io.basc.framework.util.stream;

import java.util.Objects;

public interface LongConsumerProcessor<E extends Throwable> {
	void process(long source) throws E;

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
	default LongConsumerProcessor<E> andThen(LongConsumerProcessor<? extends E> after) {
		Objects.requireNonNull(after);
		return (long t) -> {
			process(t);
			after.process(t);
		};
	}

	default Processor<Long, Void, E> toProcessor() {
		return new Processor<Long, Void, E>() {

			@Override
			public Void process(Long source) throws E {
				LongConsumerProcessor.this.process(source);
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
	default LongConsumerProcessor<E> beforeNesting(LongConsumerProcessor<E> processor) {
		if (processor == null) {
			return this;
		}

		return (long t) -> {
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
	default LongConsumerProcessor<E> afterNesting(LongConsumerProcessor<E> processor) {
		if (processor == null) {
			return this;
		}

		return (long t) -> {
			try {
				processor.process(t);
			} finally {
				process(t);
			}
		};
	}
}
