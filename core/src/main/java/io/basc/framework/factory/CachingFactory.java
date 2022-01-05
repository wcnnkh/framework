package io.basc.framework.factory;

import java.util.function.Supplier;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;

public class CachingFactory<T, E extends Throwable> implements Factory<T, E> {
	private final Factory<T, E> factory;
	private final Object lock;
	private volatile Supplier<T> supplier;

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
		if (factory instanceof CachingFactory) {
			return factory.create();
		} else {
			if (supplier == null) {
				synchronized (lock == null ? this.factory : this.lock) {
					if (supplier == null) {
						T instance = factory.create();
						this.supplier = () -> instance;
					}
				}
			}
			return supplier.get();
		}
	}

	@Override
	public Factory<T, E> single() {
		return this;
	}
}
