package scw.retry.context;

import scw.lang.Nullable;
import scw.retry.RetryContext;
import scw.util.attribute.support.SimpleAttributes;

public class DefaultRetryContext extends SimpleAttributes<String, Object> implements RetryContext {
	private RetryContext parent;
	private int retryCount;
	private Throwable lastThrowable;
	private boolean exhaustedOnly;
	
	public DefaultRetryContext(@Nullable RetryContext parent){
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
		if(lastThrowable != null){
			retryCount ++;
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
