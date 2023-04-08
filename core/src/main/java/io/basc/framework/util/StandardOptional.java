package io.basc.framework.util;

import java.util.function.Supplier;

import io.basc.framework.lang.Nullable;

public class StandardOptional<T> implements Optional<T> {
	private final Supplier<? extends T> valueSupplier;

	public StandardOptional(@Nullable Supplier<? extends T> valueSupplier) {
		this.valueSupplier = valueSupplier;
	}

	@Override
	public T orElse(T other) {
		T value = valueSupplier.get();
		return value == null ? other : value;
	}
}
