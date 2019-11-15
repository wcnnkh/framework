package scw.data.memory;

import scw.core.exception.NotSupportException;

public class DefaultMemoryData extends AbstractMemoryData {
	private volatile Object value;

	public long incr(long incr, long initialValue) {
		throw new NotSupportException("incr");
	}

	public long decr(long incr, long initialValue) {
		throw new NotSupportException("decr");
	}

	public long incr(long delta) {
		throw new NotSupportException("incr");
	}

	public long decr(long delta) {
		throw new NotSupportException("decr");
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
