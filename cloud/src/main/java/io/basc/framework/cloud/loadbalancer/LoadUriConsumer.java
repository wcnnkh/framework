package io.basc.framework.cloud.loadbalancer;

import java.net.URI;

import io.basc.framework.cloud.Service;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.retry.ExhaustedRetryException;
import io.basc.framework.util.retry.RetryContext;

public interface LoadUriConsumer<T, E extends Throwable> {
	T accept(RetryContext context, @Nullable Service server, URI uri) throws E, ExhaustedRetryException;
}
