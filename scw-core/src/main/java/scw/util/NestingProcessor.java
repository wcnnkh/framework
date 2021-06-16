package scw.util;

public class NestingProcessor<F, P, S, T, E extends Throwable> implements Processor<P, T, E> {
	private final Processor<P, ? extends S, ? extends E> processor;
	private final Processor<S, ? extends T, ? extends E> nextProcessor;
	private final Callback<S, ? extends E> finallyCallback;

	public NestingProcessor(Processor<P, ? extends S, ? extends E> processor,
			Processor<S, ? extends T, ? extends E> nextProcessor, Callback<S, ? extends E> finallyCallback) {
		this.processor = processor;
		this.nextProcessor = nextProcessor;
		this.finallyCallback = finallyCallback;
	}

	@Override
	public T process(P source) throws E {
		S s = processor.process(source);
		try {
			return nextProcessor.process(s);
		} finally {
			finallyCallback.call(s);
		}
	}
}
