package scw.util;

import scw.core.Assert;
import scw.core.utils.ObjectUtils;

public class Wrapper<W> implements Decorator{
	protected final W wrappedTarget;

	public Wrapper(W wrappedTarget) {
		Assert.requiredArgument(wrappedTarget != null, "wrappedTarget");
		this.wrappedTarget = wrappedTarget;
	}
	
	@Override
	public <T> T getDelegate(Class<T> targetType) {
		return XUtils.getDelegate(wrappedTarget, targetType);
	}

	@Override
	public String toString() {
		return wrappedTarget.toString();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof Wrapper) {
			return ObjectUtils.nullSafeEquals(wrappedTarget, ((Wrapper) obj).wrappedTarget);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return wrappedTarget.hashCode();
	}
}
