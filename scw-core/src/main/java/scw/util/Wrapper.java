package scw.util;

import scw.core.Assert;
import scw.core.utils.ObjectUtils;

public class Wrapper<W> implements Target{
	protected final W wrappedTarget;

	public Wrapper(W wrappedTarget) {
		Assert.requiredArgument(wrappedTarget != null, "wrappedTarget");
		this.wrappedTarget = wrappedTarget;
	}
	
	@Override
	public <T> T getTarget(Class<T> targetType) {
		return XUtils.getTarget(wrappedTarget, targetType);
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
