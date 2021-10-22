package io.basc.framework.util.stream;

import io.basc.framework.lang.Nullable;

public class NestingProcessor<P, S, T, E extends Throwable> implements
		Processor<P, T, E> {
	private final Processor<P, ? extends S, ? extends E> processor;
	private final Processor<S, ? extends T, ? extends E> nextProcessor;
	private final ConsumerProcessor<S, ? extends E> closeProcessor;

	public NestingProcessor(Processor<P, ? extends S, ? extends E> processor,
			Processor<S, ? extends T, ? extends E> nextProcessor,
			@Nullable ConsumerProcessor<S, ? extends E> closeProcessor) {
		this.processor = processor;
		this.nextProcessor = nextProcessor;
		this.closeProcessor = closeProcessor;
	}

	@Override
	public T process(P source) throws E {
		S s = processor.process(source);
		try {
			return nextProcessor.process(s);
		} finally {
			if(closeProcessor != null){
				closeProcessor.process(s);
			}
		}
	}
}
