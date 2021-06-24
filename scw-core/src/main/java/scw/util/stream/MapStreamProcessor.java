package scw.util.stream;

public class MapStreamProcessor<A, T, E extends Throwable> extends AbstractStreamProcessor<T, E> {
	private final CallableProcessor<A, E> sourceProcessor;
	private final Processor<A, T, E> processor;

	public MapStreamProcessor(CallableProcessor<A, E> sourceProcessor, Processor<A, T, E> processor) {
		this.sourceProcessor = sourceProcessor;
		this.processor = processor;
	}

	@Override
	public T process() throws E {
		A source = sourceProcessor.process();
		return processor.process(source);
	}

	@Override
	public <S> StreamProcessor<S, E> map(Processor<T, S, E> processor) {
		Processor<A, S, E> mapProcessor = this.processor.afterProcess(processor, (s) -> {
			close();
		});
		return new MapStreamProcessor<A, S, E>(sourceProcessor, mapProcessor);
	}
}
