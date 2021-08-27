package io.basc.framework.util.stream;

public interface CallbackProcessor<E extends Throwable> {
	void process() throws E;

	default CallbackProcessor<E> beforeProcess(
			CallbackProcessor<? extends E> processor) {
		if (processor == null) {
			return this;
		}

		final CallbackProcessor<E> self = this;
		return new CallbackProcessor<E>() {

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
	default CallbackProcessor<E> afterProcess(
			CallbackProcessor<? extends E> processor) {
		if (processor == null) {
			return this;
		}

		final CallbackProcessor<E> self = this;
		return new CallbackProcessor<E>() {

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