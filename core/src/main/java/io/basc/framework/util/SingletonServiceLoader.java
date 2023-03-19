package io.basc.framework.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.basc.framework.lang.Nullable;

public final class SingletonServiceLoader<S> extends CacheableSupplier<S> implements ServiceLoader<S> {

	public SingletonServiceLoader(S service) {
		super(service);
	}

	public SingletonServiceLoader(Supplier<? extends S> supplier, @Nullable Object lock) {
		super(supplier, lock);
	}

	@Override
	public S last() {
		return get();
	}

	@Override
	public S first() {
		return get();
	}

	@Override
	public List<S> toList() {
		return Arrays.asList(get());
	}

	@Override
	public Stream<S> stream() {
		return toList().stream();
	}

	@Override
	public Cursor<S> iterator() {
		return Cursor.of(get());
	}
}
