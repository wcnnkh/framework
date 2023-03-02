package io.basc.framework.cloud.loadbalancer;

import io.basc.framework.lang.Nullable;
import io.basc.framework.retry.ExhaustedRetryException;
import io.basc.framework.retry.RetryContext;

public interface LoadConsumer<S, T, E extends Throwable> {
	T accept(RetryContext context, @Nullable Server<S> server) throws E, ExhaustedRetryException;
}
