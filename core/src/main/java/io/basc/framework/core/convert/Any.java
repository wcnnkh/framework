package io.basc.framework.core.convert;

public class Any extends Data<Object> implements Source {
	private static final long serialVersionUID = 1L;

	@Override
	public Any any() {
		return this;
	}
}
