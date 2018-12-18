package scw.common;

final class ContextInfo<T> {
	private T value;
	private int count;

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public int getCount() {
		return count;
	}

	public void incrCount() {
		count++;
	}

	public void decrCount() {
		count--;
	}
}
