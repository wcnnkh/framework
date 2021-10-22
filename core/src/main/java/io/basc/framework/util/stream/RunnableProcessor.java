package io.basc.framework.util.stream;

/**
 * @see Runnable
 * @author shuchaowen
 *
 * @param <E>
 */
public interface RunnableProcessor<E extends Throwable> {
	void process() throws E;

	default RunnableProcessor<E> beforeProcess(
			RunnableProcessor<? extends E> processor) {
		if (processor == null) {
			return this;
		}

		final RunnableProcessor<E> self = this;
		return new RunnableProcessor<E>() {

			@Override
			public void process() throws E {
				try {
					processor.process();
				} finally {
					self.process();
				}
			}
		};
	}

	/**
	 * 在执行器外部追加指定的执行器
	 * @param processor
	 * @return
	 */
	default RunnableProcessor<E> afterProcess(
			RunnableProcessor<? extends E> processor) {
		if (processor == null) {
			return this;
		}

		final RunnableProcessor<E> self = this;
		return new RunnableProcessor<E>() {

			@Override
			public void process() throws E {
				try {
					self.process();
				} finally {
					processor.process();
				}
			}
		};
	}
}