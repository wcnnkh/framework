package io.basc.framework.cloud.loadbalancer;

import java.net.URI;

import io.basc.framework.cloud.Service;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.util.retry.ExhaustedRetryException;
import io.basc.framework.util.retry.RetryOperations;

public interface DiscoveryLoadBalancer extends LoadBalancer<Service> {

	default <V, E extends Throwable> V execute(URI uri, RetryOperations retryOperations, LoadUriConsumer<V, E> consumer)
			throws E, ExhaustedRetryException {
		return execute(uri.getHost(), retryOperations, (context, server) -> {
			if (server == null) {
				return consumer.accept(context, server, uri);
			}

			UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uri);
			builder = builder.host(server.getHost());
			int port = builder.build().getPort();
			if (port == -1) {
				builder = builder.port(server.getPort());
			}
			return consumer.accept(context, server, builder.build().toUri());
		});
	}
}
