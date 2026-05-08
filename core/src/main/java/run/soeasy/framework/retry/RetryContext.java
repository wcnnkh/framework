package run.soeasy.framework.retry;

import run.soeasy.framework.core.domain.ParentDiscover;
import run.soeasy.framework.core.exchange.KeyValueRegistry;

public interface RetryContext extends ParentDiscover<RetryContext> {
	KeyValueRegistry<String, Object> getAttributes();

	long getRetryCount();

	Throwable getLastThrowable();

	void setExhaustedOnly();

	boolean isExhaustedOnly();
}
