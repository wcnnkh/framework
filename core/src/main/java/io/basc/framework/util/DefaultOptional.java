package io.basc.framework.util;

import java.io.Serializable;

public final class DefaultOptional<T> extends AbstractOptional<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private final T value;

	public DefaultOptional(T value) {
		this.value = value;
	}

	protected T getValue() {
		return value;
	}
}