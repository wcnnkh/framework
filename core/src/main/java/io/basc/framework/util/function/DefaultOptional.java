package io.basc.framework.util.function;

import java.io.Serializable;

import lombok.ToString;

@ToString
public class DefaultOptional<T> implements Optional<T>, Serializable {
	private static final long serialVersionUID = 1L;

	public static final DefaultOptional<Object> EMPTY = new DefaultOptional<>(null);

	private final T value;

	public DefaultOptional(T value) {
		this.value = value;
	}

	@Override
	public T orElse(T other) {
		return value == null ? other : value;
	}
}