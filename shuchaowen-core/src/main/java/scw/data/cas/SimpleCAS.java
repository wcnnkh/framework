package scw.data.cas;

public final class SimpleCAS<T> extends AbstractCAS<T> {
	private final long cas;
	private final T value;

	public SimpleCAS(long cas, T value) {
		this.cas = cas;
		this.value = value;
	}

	public long getCas() {
		return cas;
	}

	public T getValue() {
		return value;
	}
}
