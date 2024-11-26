package io.basc.framework.cloud.loadbalancer;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.retry.ExhaustedRetryException;
import io.basc.framework.util.retry.RetryContext;

public interface LoadConsumer<S extends Node, T, E extends Throwable> {
	T accept(RetryContext context, @Nullable S service) throws E, ExhaustedRetryException;
}
