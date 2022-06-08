package io.basc.framework.util.stream;

import io.basc.framework.util.Wrapper;

public class StreamProcessorWrapper<W extends StreamProcessor<T, E>, T, E extends Throwable> extends Wrapper<W>
		implements StreamProcessor<T, E> {

	public StreamProcessorWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public T process() throws E {
		return wrappedTarget.process();
	}

	@Override
	public <S> StreamProcessor<S, E> map(Processor<T, ? extends S, ? extends E> processor) {
		return wrappedTarget.map(processor);
	}

	@Override
	public StreamProcessor<T, E> onClose(RunnableProcessor<E> closeProcessor) {
		return wrappedTarget.onClose(closeProcessor);
	}

	@Override
	public void close() throws E {
		wrappedTarget.close();
	}

	@Override
	public <TE extends Throwable> StreamProcessor<T, TE> exceptionConvert(Processor<Throwable, TE, ? extends TE> exceptionConverter) {
		return wrappedTarget.exceptionConvert(exceptionConverter);
	}

	@Override
	public void process(ConsumerProcessor<T, ? extends E> callback) throws E {
		wrappedTarget.process(callback);
	}

	@Override
	public <V> V process(Processor<T, ? extends V, ? extends E> processor) throws E {
		return wrappedTarget.process(processor);
	}

	@Override
	public boolean isAutoClose() {
		return wrappedTarget.isAutoClose();
	}

	@Override
	public void setAutoClose(boolean autoClose) {
		wrappedTarget.setAutoClose(autoClose);
	}

	@Override
	public boolean isClosed() {
		return wrappedTarget.isClosed();
	}
}
