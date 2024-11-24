package io.basc.framework.util.function;

import java.util.function.Supplier;

import io.basc.framework.util.Assert;
import io.basc.framework.util.ObjectUtils;

public class CacheableSource<T, E extends Throwable> implements Source<T, E> {
	private Source<? extends T, ? extends E> source;
	private Object lock;
	private volatile Supplier<T> caching;

	public CacheableSource(T source) {
		this.caching = new StaticSupplier<T>(source);
	}

	public CacheableSource(Source<? extends T, ? extends E> source, Object lock) {
		Assert.requiredArgument(source != null, "source");
		this.lock = lock;
		this.source = source;
	}

	@Override
	public T get() throws E {
		if (this.caching == null && this.source != null) {
			synchronized (this.lock == null ? this : this.lock) {
				if (this.caching == null) {
					T value = this.source.get();
					this.caching = new StaticSupplier<>(value);
				}
			}
		}
		return this.caching.get();
	}

	public void reload() throws E {
		if (this.caching != null && this.source != null) {
			synchronized (this.lock == null ? this : this.lock) {
				if (this.caching != null) {
					this.caching = new StaticSupplier<T>(this.source.get());
				}
			}
		}
	}

	@Override
	public String toString() {
		return caching == null ? source.toString() : caching.toString();
	}

	@Override
	public int hashCode() {
		return caching == null ? source.hashCode() : caching.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof CacheableSource) {
			CacheableSource<?, ?> other = (CacheableSource<?, ?>) obj;
			if (caching != null && other.caching != null) {
				if (ObjectUtils.equals(caching, other.caching)) {
					return true;
				}
			}
			return ObjectUtils.equals(source, other.source);
		}
		return false;
	}
}
