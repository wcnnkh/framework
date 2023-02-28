package io.basc.framework.util;

import io.basc.framework.lang.Nullable;

public class DecoratedConsumeProcessor<S, E extends Throwable> implements ConsumeProcessor<S, E> {
	private DecoratedConsumeProcessor<S, E> beforeProcessor;
	private DecoratedConsumeProcessor<S, E> afterProcessor;
	private final ConsumeProcessor<? super S, ? extends E> processor;

	public DecoratedConsumeProcessor() {
		this(null);
	}

	public DecoratedConsumeProcessor(@Nullable ConsumeProcessor<? super S, ? extends E> processor) {
		this.processor = processor;
	}

	@Override
	public void process(S source) throws E {
		if (beforeProcessor != null) {
			beforeProcessor.process(source);
		}
		try {
			if (processor != null) {
				processor.process(source);
			}
		} finally {
			if (afterProcessor != null) {
				afterProcessor.process(source);
			}
		}
	}

	/**
	 * 之前
	 * 
	 * @param processor
	 * @return
	 */
	public DecoratedConsumeProcessor<S, E> before(ConsumeProcessor<? super S, ? extends E> processor) {
		if (beforeProcessor == null) {
			this.beforeProcessor = new DecoratedConsumeProcessor<>(processor);
		} else {
			this.beforeProcessor = this.beforeProcessor.before(processor);
		}
		return this;
	}

	/**
	 * 之后
	 * 
	 * @param processor
	 * @return
	 */
	public DecoratedConsumeProcessor<S, E> after(ConsumeProcessor<? super S, ? extends E> processor) {
		if (this.afterProcessor == null) {
			this.afterProcessor = new DecoratedConsumeProcessor<>(processor);
		} else {
			this.beforeProcessor = this.afterProcessor.before(processor);
		}
		return this;
	}
}
