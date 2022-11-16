package io.basc.framework.util;

import java.util.function.Supplier;

import io.basc.framework.lang.Nullable;

public class SharedOptional<T> extends AbstractOptional<T> {
	private final Supplier<? extends T> valueSupplier;

	public SharedOptional(@Nullable Supplier<? extends T> valueSupplier) {
		this.valueSupplier = valueSupplier;
	}

	@Override
	protected T getValue() {
		return valueSupplier == null ? null : valueSupplier.get();
	}
}
