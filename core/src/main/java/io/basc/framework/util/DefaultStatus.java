package io.basc.framework.util;

import java.io.Serializable;

public class DefaultStatus<T> implements Status<T>, Serializable {
	private static final long serialVersionUID = 1L;
	private final boolean active;
	private final java.util.function.Supplier<T> result;

	public DefaultStatus(boolean active, java.util.function.Supplier<T> result) {
		this.active = active;
		this.result = result;
	}

	public DefaultStatus(boolean active, T result) {
		this(active, new StaticSupplier<T>(result));
	}

	public boolean isActive() {
		return active;
	}

	@Override
	public T get() {
		return result.get();
	}
}
