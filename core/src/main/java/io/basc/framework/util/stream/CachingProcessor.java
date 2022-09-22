package io.basc.framework.util.stream;

import java.util.function.Supplier;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;

public class CachingProcessor<T, E extends Throwable> implements CallableProcessor<T, E> {
	private final CallableProcessor<T, E> processor;
	private final Object lock;
	private volatile Supplier<T> caching;

	public CachingProcessor(CallableProcessor<T, E> processor) {
		this(null, processor);
	}

	public CachingProcessor(@Nullable Object lock, CallableProcessor<T, E> processor) {
		Assert.requiredArgument(processor != null, "processor");
		this.lock = lock;
		this.processor = processor;
	}

	@Override
	public T process() throws E {
		if (this.caching == null) {
			synchronized (this.lock == null ? this : this.lock) {
				if (this.caching == null) {
					T value = this.processor.process();
					this.caching = () -> value;
				}
			}
		}
		return this.caching.get();
	}

	public void clear() {
		if (this.caching != null) {
			synchronized (this.lock == null ? this : this.lock) {
				if (this.caching != null) {
					this.caching = null;
				}
			}
		}
	}

	@Override
	public CallableProcessor<T, E> caching() {
		return this;
	}

	public boolean isEmpty() {
		return caching == null;
	}
}
