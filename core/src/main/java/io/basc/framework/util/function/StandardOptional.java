package io.basc.framework.util.function;

import java.util.function.Supplier;

public class StandardOptional<T> implements Optional<T> {
	private final Supplier<? extends T> valueSupplier;

	public StandardOptional(Supplier<? extends T> valueSupplier) {
		this.valueSupplier = valueSupplier;
	}

	@Override
	public T orElse(T other) {
		T value = valueSupplier.get();
		return value == null ? other : value;
	}
}
