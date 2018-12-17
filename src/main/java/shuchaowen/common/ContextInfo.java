package shuchaowen.common;

public final class ContextInfo<T> {
	private T value;
	private int count;

	protected ContextInfo() {
	};

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public int getCount() {
		return count;
	}

	protected void incrCount() {
		count++;
	}

	protected void decrCount() {
		count--;
	}
}
