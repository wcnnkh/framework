package io.basc.framework.util;

import java.util.function.Supplier;

import io.basc.framework.lang.Nullable;

public class CacheableSource<T, E extends Throwable> implements Source<T, E> {
	private final Source<? extends T, ? extends E> source;
	private final Object lock;
	private volatile Supplier<T> caching;

	public CacheableSource(Source<? extends T, ? extends E> source) {
		this(null, source);
	}

	public CacheableSource(@Nullable Object lock, Source<? extends T, ? extends E> source) {
		Assert.requiredArgument(source != null, "source");
		this.lock = lock;
		this.source = source;
	}

	@Override
	public T get() throws E {
		if (this.caching == null) {
			synchronized (this.lock == null ? this : this.lock) {
				if (this.caching == null) {
					T value = this.source.get();
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

	public boolean isEmpty() {
		return caching == null;
	}
}
