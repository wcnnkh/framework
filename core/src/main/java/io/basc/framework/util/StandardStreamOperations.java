package io.basc.framework.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import io.basc.framework.lang.Nullable;

public class StandardStreamOperations<T, E extends Throwable, C extends StandardStreamOperations<T, E, C>>
		extends StandardCloser<T, E, C> implements StreamOperations<T, E> {
	private final Processor<? super C, ? extends T, ? extends E> sourceProcesor;
	private List<ConsumeProcessor<? super T, ? extends E>> consumers;
	private Supplier<? extends String> toString;

	public StandardStreamOperations(Source<? extends T, ? extends E> source) {
		this(source, null, null);
	}

	public StandardStreamOperations(Source<? extends T, ? extends E> source,
			@Nullable ConsumeProcessor<? super T, ? extends E> closeProcessor,
			@Nullable RunnableProcessor<? extends E> closeHandler) {
		this(new Processor<C, T, E>() {

			@Override
			public T process(C operations) throws E {
				return source.get();
			}

			@Override
			public String toString() {
				return source.toString();
			}
		}, closeProcessor, closeHandler);
	}

	public StandardStreamOperations(Processor<? super C, ? extends T, ? extends E> sourceProcesor) {
		this(sourceProcesor, null, null);
	}

	public StandardStreamOperations(Processor<? super C, ? extends T, ? extends E> sourceProcesor,
			@Nullable ConsumeProcessor<? super T, ? extends E> closeProcessor,
			@Nullable RunnableProcessor<? extends E> closeHandler) {
		super(closeHandler, closeProcessor);
		this.sourceProcesor = sourceProcesor;
	}

	public <S> StandardStreamOperations(StreamOperations<S, ? extends E> sourceStreamOperations,
			Processor<? super S, ? extends T, ? extends E> processor,
			@Nullable ConsumeProcessor<? super T, ? extends E> closeProcessor,
			@Nullable RunnableProcessor<? extends E> closeHandler) {
		this(new Processor<C, T, E>() {

			@Override
			public T process(C operations) throws E {
				S source = sourceStreamOperations.get();
				try {
					return processor.process(source);
				} catch (Throwable e) {
					sourceStreamOperations.close(source);
					throw e;
				} finally {
					operations.onClose(() -> sourceStreamOperations.close(source))
							.onClose(() -> sourceStreamOperations.close());
				}
			}

			@Override
			public String toString() {
				return sourceStreamOperations.toString();
			}
		}, closeProcessor, closeHandler);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get() throws E {
		T target = sourceProcesor.process((C) this);
		if (consumers != null) {
			try {
				for (ConsumeProcessor<? super T, ? extends E> consumer : consumers) {
					consumer.process(target);
				}
			} catch (Throwable e) {
				try {
					close(target);
				} finally {
					close();
				}
				throw e;
			}
		}
		return target;
	}

	@SuppressWarnings("unchecked")
	public C after(ConsumeProcessor<? super T, ? extends E> consumer) {
		if (consumers == null) {
			consumers = new ArrayList<>();
		}

		consumers.add(consumer);
		return (C) this;
	}

	public void setToString(Supplier<? extends String> toString) {
		this.toString = toString;
	}

	@Override
	public String toString() {
		return toString == null ? sourceProcesor.toString() : toString.toString();
	}
}
