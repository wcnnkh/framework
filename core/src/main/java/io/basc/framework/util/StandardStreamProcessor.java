package io.basc.framework.util;

public class StandardStreamProcessor<S, T, E extends Throwable, C extends StreamProcessor<S, T, E>>
		extends StandardCloser<T, E, C> implements StreamProcessor<S, T, E> {
	private final Processor<? super S, ? extends T, ? extends E> processor;

	public StandardStreamProcessor(Processor<? super S, ? extends T, ? extends E> processor) {
		this.processor = processor;
	}

	@Override
	public T process(S source) throws E {
		return processor.process(source);
	}
}
