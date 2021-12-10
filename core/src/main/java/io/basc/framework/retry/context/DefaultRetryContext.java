package io.basc.framework.retry.context;

import io.basc.framework.lang.Nullable;
import io.basc.framework.retry.RetryContext;
import io.basc.framework.util.attribute.SimpleAttributes;

public class DefaultRetryContext extends SimpleAttributes<String, Object> implements RetryContext {
	private RetryContext parent;
	private int retryCount;
	private Throwable lastThrowable;
	private boolean exhaustedOnly;

	public DefaultRetryContext(@Nullable RetryContext parent) {
		this.parent = parent;
	}

	public RetryContext getParent() {
		return parent;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public Throwable getLastThrowable() {
		return lastThrowable;
	}

	public Throwable setLastThrowable(Throwable lastThrowable) {
		Throwable old = this.lastThrowable;
		this.lastThrowable = lastThrowable;
		if (lastThrowable != null) {
			retryCount++;
		}
		return old;
	}

	public boolean isExhaustedOnly() {
		return exhaustedOnly;
	}

	public void setExhaustedOnly() {
		this.exhaustedOnly = true;
	}
}
