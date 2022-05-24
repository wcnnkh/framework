package io.basc.framework.util.stream;

public class ExceptionConvertStreamProcessor<SE extends Throwable, T, E extends Throwable>
		implements StreamProcessor<T, E> {
	private final StreamProcessor<T, SE> streamProcessor;
	private final Processor<Throwable, E, ? extends E> excpetionConverter;

	public ExceptionConvertStreamProcessor(StreamProcessor<T, SE> streamProcessor,
			Processor<Throwable, E, ? extends E> excpetionConverter) {
		this.streamProcessor = streamProcessor;
		this.excpetionConverter = excpetionConverter;
	}

	@Override
	public T process() throws E {
		try {
			return streamProcessor.process();
		} catch (Throwable e) {
			throw excpetionConverter.process(e);
		}
	}

	@Override
	public <S> StreamProcessor<S, E> map(Processor<T, ? extends S, ? extends E> processor) {
		return new ExceptionConvertStreamProcessor<>(streamProcessor, excpetionConverter).map(processor);
	}

	@Override
	public StreamProcessor<T, E> onClose(RunnableProcessor<E> closeProcessor) {
		return new ExceptionConvertStreamProcessor<>(streamProcessor, excpetionConverter).onClose(() -> {
			closeProcessor.process();
		});
	}

	@Override
	public void close() throws E {
		try {
			this.streamProcessor.close();
		} catch (Throwable e) {
			throw excpetionConverter.process(e);
		}
	}

	@Override
	public boolean isAutoClose() {
		return streamProcessor.isAutoClose();
	}

	@Override
	public void setAutoClose(boolean autoClose) {
		streamProcessor.setAutoClose(autoClose);
	}

	@Override
	public boolean isClosed() {
		return streamProcessor.isClosed();
	}
}
