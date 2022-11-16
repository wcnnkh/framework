package io.basc.framework.util;

import java.util.function.Supplier;

import io.basc.framework.lang.Nullable;

public final class CacheableSupplier<T> implements Supplier<T> {
	private final Supplier<? extends T> supplier;
	private final Object lock;
	private volatile Supplier<T> caching;

	public CacheableSupplier(Supplier<? extends T> processor) {
		this(null, processor);
	}

	public CacheableSupplier(@Nullable Object lock, Supplier<? extends T> supplier) {
		Assert.requiredArgument(supplier != null, "supplier");
		this.lock = lock;
		this.supplier = supplier;
	}

	public Supplier<? extends T> getSourceSupplier() {
		return this.supplier;
	}

	@Override
	public T get() {
		if (this.caching == null) {
			synchronized (this.lock == null ? this : this.lock) {
				if (this.caching == null) {
					T value = this.supplier.get();
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
