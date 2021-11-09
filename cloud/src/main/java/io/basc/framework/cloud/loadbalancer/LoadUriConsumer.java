package io.basc.framework.cloud.loadbalancer;

import java.net.URI;

import io.basc.framework.cloud.ServiceInstance;
import io.basc.framework.lang.Nullable;
import io.basc.framework.retry.ExhaustedRetryException;
import io.basc.framework.retry.RetryContext;

public interface LoadUriConsumer<T, E extends Throwable> {
	/**
	 * @param context
	 * @param server  如果为空说明未找到服务，但只会执行一次
	 * @param uri
	 * @return
	 * @throws E
	 * @throws ExhaustedRetryException
	 */
	T accept(RetryContext context, @Nullable Server<ServiceInstance> server, URI uri) throws E, ExhaustedRetryException;
}
