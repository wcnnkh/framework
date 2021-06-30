package scw.util.stream;

public abstract class AbstractStreamProcessor<T, E extends Throwable> implements StreamProcessor<T, E> {
	private CallbackProcessor<E> closeProcessor;

	@Override
	public StreamProcessor<T, E> onClose(CallbackProcessor<E> closeProcessor) {
		if (this.closeProcessor == null) {
			this.closeProcessor = closeProcessor;
		} else {
			this.closeProcessor = this.closeProcessor.afterProcess(closeProcessor);
		}
		return this;
	}

	@Override
	public void close() throws E {
		if (this.closeProcessor != null) {
			this.closeProcessor.process();
		}
	}

}
