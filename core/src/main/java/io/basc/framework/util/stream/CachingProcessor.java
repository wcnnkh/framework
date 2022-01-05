package io.basc.framework.util.stream;

import java.util.function.Supplier;

import io.basc.framework.convert.Converter;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;

public class CachingProcessor<S, T, E extends Throwable> implements Processor<S, T, E> {
	private final Processor<S, T, E> processor;
	private final Object lock;
	private volatile Supplier<T> caching;

	public CachingProcessor(Processor<S, T, E> processor) {
		this(null, processor);
	}

	public CachingProcessor(@Nullable Object lock, Processor<S, T, E> processor) {
		Assert.requiredArgument(processor != null, "processor");
		this.lock = lock;
		this.processor = processor;
	}

	@Override
	public T process(S source) throws E {
		if (this.processor instanceof CachingProcessor) {
			return this.processor.process(source);
		} else {
			if (this.caching == null) {
				synchronized (this.lock == null ? this.processor : this.lock) {
					if (this.caching == null) {
						T value = this.processor.process(source);
						this.caching = () -> value;
					}
				}
			}
			return this.caching.get();
		}
	}

	public void clear() {
		if (this.caching != null) {
			synchronized (this.lock == null ? this.processor : this.lock) {
				if (this.caching != null) {
					this.caching = null;
				}
			}
		}
	}

	@Override
	public <A> Processor<S, A, E> afterProcess(Processor<T, ? extends A, ? extends E> processor,
			ConsumerProcessor<T, ? extends E> closeProcessor) {
		Processor<S, A, E> p = this.processor.afterProcess(processor, closeProcessor);
		if (p instanceof CachingProcessor) {
			return p;
		} else {
			return new CachingProcessor<S, A, E>(this.lock, p);
		}
	}

	@Override
	public <B> Processor<B, T, E> beforeProcess(Processor<B, ? extends S, ? extends E> processor,
			ConsumerProcessor<S, ? extends E> closeProcessor) {
		Processor<B, T, E> p = this.processor.beforeProcess(processor, closeProcessor);
		if (p instanceof CachingProcessor) {
			return p;
		} else {
			return new CachingProcessor<B, T, E>(this.lock, p);
		}
	}

	@Override
	public <X extends Throwable> Processor<S, T, X> exceptionConvert(Converter<Throwable, X> exceptionConverter) {
		Processor<S, T, X> p = this.processor.exceptionConvert(exceptionConverter);
		if (p instanceof CachingProcessor) {
			return p;
		} else {
			return new CachingProcessor<S, T, X>(this.lock, p);
		}
	}

	@Override
	public Processor<S, T, E> caching() {
		return this;
	}
}
