package io.basc.framework.util.stream;

public class DefaultStreamProcessor<T, E extends Throwable> extends AbstractStreamProcessor<T, E> {
	private final CallableProcessor<T, E> processor;

	public DefaultStreamProcessor(CallableProcessor<T, E> processor) {
		this.processor = processor;
	}

	@Override
	public T process() throws E {
		return processor.process();
	}

	@Override
	public <S> StreamProcessor<S, E> map(Processor<T, ? extends S, ? extends E> processor) {
		return new MapStreamProcessor<T, S, E>(this.processor, (t) -> {
			return processor.process(t);
		}).onClose(() -> close());
	}
}
