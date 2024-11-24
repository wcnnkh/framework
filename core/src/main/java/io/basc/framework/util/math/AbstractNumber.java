package io.basc.framework.util.math;

public abstract class AbstractNumber extends Number implements NumberValue {
	private static final long serialVersionUID = 1L;

	@Override
	public int intValue() {
		return getAsInt();
	}

	@Override
	public long longValue() {
		return getAsLong();
	}

	@Override
	public float floatValue() {
		return getAsFloat();
	}

	@Override
	public double doubleValue() {
		return getAsDouble();
	}

	@Override
	public Number getAsNumber() {
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof NumberValue) {
			return compareTo((NumberValue) obj) == 0;
		}
		return false;
	}
}
