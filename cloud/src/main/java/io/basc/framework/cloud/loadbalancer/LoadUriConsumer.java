package io.basc.framework.cloud.loadbalancer;

import java.net.URI;

import io.basc.framework.cloud.ServiceInstance;
import io.basc.framework.lang.Nullable;
import io.basc.framework.retry.ExhaustedRetryException;
import io.basc.framework.retry.RetryContext;

public interface LoadUriConsumer<T, E extends Throwable> {
	T accept(RetryContext context, @Nullable Server<ServiceInstance> server, URI uri) throws E, ExhaustedRetryException;
}
