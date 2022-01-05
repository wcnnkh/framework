package io.basc.framework.factory;

import java.util.function.Supplier;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;

public class CachingFactory<T, E extends Throwable> implements Factory<T, E> {
	private final Factory<T, E> factory;
	private final Object lock;
	private volatile Supplier<T> caching;

	public CachingFactory(Factory<T, E> factory) {
		this(null, factory);
	}

	public CachingFactory(@Nullable Object lock, Factory<T, E> factory) {
		Assert.requiredArgument(factory != null, "factory");
		this.lock = lock;
		this.factory = factory;
	}

	@Override
	public T create() throws E {
		if (this.factory instanceof CachingFactory) {
			return this.factory.create();
		} else {
			if (this.caching == null) {
				synchronized (this.lock == null ? this.factory : this.lock) {
					if (this.caching == null) {
						T instance = this.factory.create();
						this.caching = () -> instance;
					}
				}
			}
			return this.caching.get();
		}
	}

	@Override
	public Factory<T, E> single() {
		return this;
	}

	public void clear() {
		if (this.caching != null) {
			synchronized (this.lock == null ? this.factory : this.lock) {
				if (this.caching != null) {
					this.caching = null;
				}
			}
		}
	}
}
