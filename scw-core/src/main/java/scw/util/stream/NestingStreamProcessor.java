package scw.util.stream;


public class NestingStreamProcessor<A, T, E extends Throwable> extends
		AbstractStreamProcessor<T, E> {
	private final CallableProcessor<A, E> sourceProcessor;
	private final Processor<A, T, E> processor;

	public NestingStreamProcessor(CallableProcessor<A, E> sourceProcessor,
			Processor<A, T, E> processor) {
		this.sourceProcessor = sourceProcessor;
		this.processor = processor;
	}

	@Override
	public T process() throws E {
		A source = sourceProcessor.process();
		try {
			return processor.process(source);
		} finally {
			close();
		}
	}

	@Override
	public <S> StreamProcessor<S, E> stream(Processor<T, S, E> processor) {
		Processor<A, S, E> nestingProcess = this.processor.afterProcess(
				processor, this);
		return new NestingStreamProcessor<A, S, E>(sourceProcessor,
				nestingProcess);
	}
}
