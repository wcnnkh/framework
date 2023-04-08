package io.basc.framework.util;

import java.util.function.Function;

import io.basc.framework.lang.Nullable;

public class StandardReturn<T> extends StandardStatus implements Return<T> {
	private static final long serialVersionUID = 1L;
	protected final T value;

	public StandardReturn(boolean success, long code, @Nullable String description, @Nullable T value) {
		super(success, code, description);
		this.value = value;
	}

	public StandardReturn(Status status, @Nullable T value) {
		super(status);
		this.value = value;
	}

	public StandardReturn(Return<T> rtn) {
		super(rtn);
		if (rtn instanceof StandardReturn) {
			StandardReturn<T> defaultReturn = (StandardReturn<T>) rtn;
			this.value = defaultReturn.value;
		} else {
			this.value = rtn.get();
		}
	}

	@Override
	public T orElse(T other) {
		return value == null ? other : value;
	}

	@Override
	public <U> Return<U> convert(Function<? super T, ? extends U> converter) {
		Assert.requiredArgument(converter != null, "converter");
		U u = converter.apply(value);
		return new StandardReturn<U>(this, u);
	}
}
