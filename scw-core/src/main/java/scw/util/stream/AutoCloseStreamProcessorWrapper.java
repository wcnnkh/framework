package scw.util.stream;

import scw.core.utils.ObjectUtils;

public class AutoCloseStreamProcessorWrapper<T, E extends Throwable> implements AutoCloseStreamProcessor<T, E> {
	private final StreamProcessor<T, E> streamProcessor;

	public AutoCloseStreamProcessorWrapper(StreamProcessor<T, E> streamProcessor) {
		this.streamProcessor = streamProcessor;
	}

	@Override
	public T process() throws E {
		try {
			return streamProcessor.process();
		} finally {
			close();
		}
	}

	@Override
	public <S> AutoCloseStreamProcessor<S, E> map(Processor<T, S, E> processor) {
		StreamProcessor<S, E> streamProcessor = this.streamProcessor.map(processor);
		if (streamProcessor instanceof AutoCloseStreamProcessor) {
			return (AutoCloseStreamProcessor<S, E>) streamProcessor;
		}
		return new AutoCloseStreamProcessorWrapper<>(streamProcessor);
	}

	@Override
	public AutoCloseStreamProcessor<T, E> onClose(CallbackProcessor<E> closeProcessor) {
		StreamProcessor<T, E> streamProcessor = this.streamProcessor.onClose(closeProcessor);
		if (streamProcessor instanceof AutoCloseStreamProcessor) {
			return (AutoCloseStreamProcessor<T, E>) streamProcessor;
		}
		return new AutoCloseStreamProcessorWrapper<>(streamProcessor);
	}

	@Override
	public void close() throws E {
		this.streamProcessor.close();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof AutoCloseStreamProcessorWrapper) {
			return ObjectUtils.nullSafeEquals(((AutoCloseStreamProcessorWrapper<?, ?>) obj).streamProcessor, this.streamProcessor);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.streamProcessor.hashCode();
	}

	@Override
	public String toString() {
		return this.streamProcessor.toString();
	}
}
