package io.basc.framework.util;

import java.util.Objects;

public class Wrapper<W> implements Decorator {
	protected final W wrappedTarget;
	private Object equalsAndHashCode;

	public Wrapper(W wrappedTarget) {
		Assert.requiredArgument(wrappedTarget != null, "wrappedTarget");
		this.wrappedTarget = wrappedTarget;
		this.equalsAndHashCode = wrappedTarget;
	}

	public W getDelegateSource() {
		return wrappedTarget;
	}

	public Object getEqualsAndHashCode() {
		return equalsAndHashCode;
	}

	public void setEqualsAndHashCode(Object equalsAndHashCode) {
		this.equalsAndHashCode = equalsAndHashCode;
	}

	@Override
	public <T> T getDelegate(Class<T> targetType) {
		return XUtils.getDelegate(wrappedTarget, targetType);
	}

	@Override
	public String toString() {
		return wrappedTarget.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof Wrapper) {
			return ObjectUtils.equals(equalsAndHashCode, ((Wrapper<?>) obj).equalsAndHashCode);
		}
		return obj.equals(equalsAndHashCode);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(equalsAndHashCode);
	}
}
