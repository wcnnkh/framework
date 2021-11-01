package io.basc.framework.util.stream;

public class DefaultStreamProcessor<T, E extends Throwable> extends AbstractStreamProcessor<T, E> {
	private final CallableProcessor<T, E> processor;

	public DefaultStreamProcessor(CallableProcessor<T, E> processor) {
		this.processor = processor;
	}

	@Override
	public T process() throws E {
		try {
			beforeProcess();
			return processor.process();
		} finally {
			afterProcess();
		}
	}
}
