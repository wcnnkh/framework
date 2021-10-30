package io.basc.framework.util.stream;

import io.basc.framework.util.Wrapper;

import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class AutoCloseStreamProcessorWrapper<T, E extends Throwable> extends Wrapper<StreamProcessor<T, E>>
		implements AutoCloseStreamProcessor<T, E> {
	private boolean closed;

	public AutoCloseStreamProcessorWrapper(StreamProcessor<T, E> streamProcessor) {
		super(streamProcessor);
	}

	@Override
	public T process() throws E {
		try {
			return wrappedTarget.process();
		} finally {
			close();
		}
	}

	@Override
	public void process(ConsumerProcessor<T, ? extends E> processor) throws E {
		try {
			processor.process(wrappedTarget.process());
		} finally {
			close();
		}
	}

	@Override
	public <V> V process(Processor<T, ? extends V, ? extends E> processor) throws E {
		try {
			return processor.process(wrappedTarget.process());
		} finally {
			close();
		}
	}

	@Override
	public <V> StreamWrapper<V> stream(Processor<T, Spliterator<V>, E> processor) throws E {
		if (wrappedTarget instanceof AutoCloseStreamProcessor) {
			return ((AutoCloseStreamProcessor<T, E>) wrappedTarget).stream(processor).onClose(() -> {
				try {
					close();
				} catch (Throwable e) {
					if (e instanceof RuntimeException) {
						throw (RuntimeException) e;
					}
					throw new StreamException(e);
				}
			});
		}

		T source = wrappedTarget.process();
		Spliterator<V> spliterator = processor.process(source);
		Stream<V> stream = StreamSupport.stream(spliterator, false).onClose(() -> {
			try {
				close();
			} catch (Throwable e) {
				if (e instanceof RuntimeException) {
					throw (RuntimeException) e;
				}
				throw new StreamException(e);
			}
		});
		return new StreamWrapper<>(stream);
	}

	@Override
	public <S> AutoCloseStreamProcessor<S, E> map(Processor<T, ? extends S, ? extends E> processor) {
		StreamProcessor<S, E> streamProcessor = this.wrappedTarget.map(processor);
		if (streamProcessor instanceof AutoCloseStreamProcessor) {
			return (AutoCloseStreamProcessor<S, E>) streamProcessor;
		}
		return new AutoCloseStreamProcessorWrapper<>(streamProcessor);
	}

	@Override
	public AutoCloseStreamProcessor<T, E> onClose(RunnableProcessor<E> closeProcessor) {
		StreamProcessor<T, E> streamProcessor = this.wrappedTarget.onClose(closeProcessor);
		if (streamProcessor instanceof AutoCloseStreamProcessor) {
			return (AutoCloseStreamProcessor<T, E>) streamProcessor;
		}
		return new AutoCloseStreamProcessorWrapper<>(streamProcessor);
	}

	@Override
	public void close() throws E {
		this.closed = true;
		this.wrappedTarget.close();
	}

	@Override
	public boolean isClosed() {
		return closed;
	}
}
