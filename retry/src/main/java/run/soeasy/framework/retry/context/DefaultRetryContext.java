package run.soeasy.framework.retry.context;

import run.soeasy.framework.core.attribute.SimpleAttributes;
import run.soeasy.framework.retry.RetryContext;

public class DefaultRetryContext extends SimpleAttributes<String, Object> implements RetryContext {
	private RetryContext parent;
	private int retryCount;
	private Throwable lastThrowable;
	private boolean exhaustedOnly;

	public DefaultRetryContext(RetryContext parent) {
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
