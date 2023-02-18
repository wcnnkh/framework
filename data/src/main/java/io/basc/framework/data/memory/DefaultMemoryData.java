package io.basc.framework.data.memory;

import io.basc.framework.lang.UnsupportedException;

public class DefaultMemoryData extends AbstractMemoryData {
	private volatile Object value;

	public long incr(long incr, long initialValue) {
		throw new UnsupportedException("incr");
	}

	public long decr(long incr, long initialValue) {
		throw new UnsupportedException("decr");
	}

	public long incr(long delta) {
		throw new UnsupportedException("incr");
	}

	public long decr(long delta) {
		throw new UnsupportedException("decr");
	}

	@Override
	protected boolean setValue(Object value) {
		this.value = value;
		return true;
	}

	@Override
	protected Object getValue() {
		return value;
	}
}
