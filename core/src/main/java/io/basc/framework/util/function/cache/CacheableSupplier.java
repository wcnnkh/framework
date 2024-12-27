package io.basc.framework.util.function.cache;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.Reloadable;
import io.basc.framework.util.Source;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.exchange.Publisher;
import lombok.NonNull;

public class CacheableSupplier<T>
		extends CacheableSource<T, RuntimeException, Source<? extends T, ? extends RuntimeException>>
		implements Supplier<T>, Reloadable {
	private static final long serialVersionUID = 1L;

	public CacheableSupplier(@NonNull Publisher<? super ChangeEvent<T>> eventPublishService,
			@NonNull ReadWriteLock readWriteLock, @NonNull Source<? extends T, ? extends RuntimeException> source) {
		super(eventPublishService, readWriteLock, source);
	}

	public CacheableSupplier(@NonNull Publisher<? super ChangeEvent<T>> eventPublishService,
			@NonNull Source<? extends T, ? extends RuntimeException> source) {
		this(eventPublishService, new ReentrantReadWriteLock(), source);
	}

	public CacheableSupplier(@NonNull Source<? extends T, ? extends RuntimeException> source) {
		this(Publisher.empty(), source);
	}

	@Override
	public T get() {
		return super.get();
	}

	@Override
	public void reload() {
		reload(true);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(get());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CacheableSupplier) {
			CacheableSupplier other = (CacheableSupplier) obj;
			return ObjectUtils.equals(get(), other.get());
		}
		return ObjectUtils.equals(get(), obj);
	}

	@Override
	public String toString() {
		return ObjectUtils.toString(get());
	}
}
