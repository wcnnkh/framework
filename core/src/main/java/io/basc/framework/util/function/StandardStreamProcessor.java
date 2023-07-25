package io.basc.framework.util.function;

import io.basc.framework.lang.Nullable;

public class StandardStreamProcessor<S, T, E extends Throwable, C extends StandardStreamProcessor<S, T, E, C>>
		extends StandardCloser<T, E, C> implements StreamProcessor<S, T, E> {
	private final Processor<? super S, ? extends T, ? extends E> processor;

	public StandardStreamProcessor(Processor<? super S, ? extends T, ? extends E> processor) {
		this(processor, null);
	}

	public StandardStreamProcessor(Processor<? super S, ? extends T, ? extends E> processor,
			@Nullable ConsumeProcessor<? super T, ? extends E> closeHandler) {
		super(closeHandler);
		this.processor = processor;
	}

	@Override
	public T process(S source) throws E {
		return processor.process(source);
	}
}
