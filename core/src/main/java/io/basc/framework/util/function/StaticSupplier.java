package io.basc.framework.util.function;

import java.io.Serializable;
import java.util.function.Supplier;

import io.basc.framework.util.ObjectUtils;

public final class StaticSupplier<T> implements Supplier<T>, Serializable {
	private static final long serialVersionUID = 1L;
	private final T source;

	public StaticSupplier(T source) {
		this.source = source;
	}

	public T get() {
		return source;
	}

	@Override
	public String toString() {
		return String.valueOf(source);
	}

	@Override
	public int hashCode() {
		return source == null ? 0 : source.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof StaticSupplier) {
			return ObjectUtils.equals(source, ((StaticSupplier<?>) obj).source);
		}
		return false;
	}
}
