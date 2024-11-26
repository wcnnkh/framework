package io.basc.framework.util.function;

import io.basc.framework.util.Endpoint;
import io.basc.framework.util.Pipeline;
import lombok.NonNull;

public class StandardStreamProcessor<S, T, E extends Throwable, C extends StandardStreamProcessor<S, T, E, C>>
		extends StandardCloser<T, E, C> implements StreamProcessor<S, T, E> {
	private final Pipeline<? super S, ? extends T, ? extends E> processor;

	public StandardStreamProcessor(@NonNull Pipeline<? super S, ? extends T, ? extends E> processor) {
		this(processor, null);
	}

	public StandardStreamProcessor(@NonNull Pipeline<? super S, ? extends T, ? extends E> processor,
			Endpoint<? super T, ? extends E> closeHandler) {
		super(closeHandler);
		this.processor = processor;
	}

	@Override
	public T process(S source) throws E {
		return processor.process(source);
	}
}
