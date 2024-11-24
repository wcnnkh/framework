package io.basc.framework.util.function;

import lombok.NonNull;

public class StandardStreamProcessor<S, T, E extends Throwable, C extends StandardStreamProcessor<S, T, E, C>>
		extends StandardCloser<T, E, C> implements StreamProcessor<S, T, E> {
	private final Processor<? super S, ? extends T, ? extends E> processor;

	public StandardStreamProcessor(@NonNull Processor<? super S, ? extends T, ? extends E> processor) {
		this(processor, null);
	}

	public StandardStreamProcessor(@NonNull Processor<? super S, ? extends T, ? extends E> processor,
			ConsumeProcessor<? super T, ? extends E> closeHandler) {
		super(closeHandler);
		this.processor = processor;
	}

	@Override
	public T process(S source) throws E {
		return processor.process(source);
	}
}
