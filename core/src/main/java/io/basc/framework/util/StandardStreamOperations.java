package io.basc.framework.util;

import java.util.ArrayList;
import java.util.List;

import io.basc.framework.lang.Nullable;

public class StandardStreamOperations<T, E extends Throwable, C extends StreamOperations<T, E>>
		extends AbstractStreamOperations<T, E, C> {
	private final Processor<? super C, ? extends T, ? extends E> sourceProcesor;
	private List<ConsumeProcessor<? super T, ? extends E>> consumers;

	public StandardStreamOperations(Source<? extends T, ? extends E> source) {
		this(source, null, null);
	}

	public StandardStreamOperations(Source<? extends T, ? extends E> source,
			@Nullable ConsumeProcessor<? super T, ? extends E> closeProcessor,
			@Nullable RunnableProcessor<? extends E> closeHandler) {
		this((e) -> source.get(), closeProcessor, closeHandler);
	}

	public StandardStreamOperations(Processor<? super C, ? extends T, ? extends E> sourceProcesor) {
		this(sourceProcesor, null, null);
	}

	public StandardStreamOperations(Processor<? super C, ? extends T, ? extends E> sourceProcesor,
			@Nullable ConsumeProcessor<? super T, ? extends E> closeProcessor,
			@Nullable RunnableProcessor<? extends E> closeHandler) {
		super(closeProcessor, closeHandler);
		this.sourceProcesor = sourceProcesor;
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
}
