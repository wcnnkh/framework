package scw.util.stream;

public class DefaultStreamProcessor<T, E extends Throwable> extends
		AbstractStreamProcessor<T, E> {
	private final CallableProcessor<T, E> processor;

	public DefaultStreamProcessor(CallableProcessor<T, E> processor) {
		this.processor = processor;
	}

	@Override
	public T process() throws E {
		return processor.process();
	}

	@Override
	public <S> StreamProcessor<S, E> stream(Processor<T, S, E> processor) {
		return new NestingStreamProcessor<T, S, E>(this.processor, processor)
				.onClose(() -> close());
	}

}
