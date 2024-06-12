package io.basc.framework.data.domain;

import java.util.function.Function;

import io.basc.framework.observe.Variable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.function.DefaultOptional;

public class CAS<V> extends DefaultOptional<V> implements Variable {
	private static final long serialVersionUID = 1L;
	private final long lastModified;

	public CAS(V value, long lastModified) {
		super(value);
		this.lastModified = lastModified;
	}

	@Override
	public long lastModified() {
		return lastModified;
	}

	public <T> CAS<T> convert(Function<? super V, ? extends T> converter) {
		Assert.requiredArgument(converter != null, "converter");
		V oldValue = orElse(null);
		T newValue = converter.apply(oldValue);
		return new CAS<T>(newValue, lastModified);
	}
}
