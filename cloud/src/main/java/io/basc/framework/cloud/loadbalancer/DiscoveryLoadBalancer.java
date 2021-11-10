package io.basc.framework.cloud.loadbalancer;

import java.net.URI;
import java.util.HashSet;

import io.basc.framework.cloud.ServiceInstance;
import io.basc.framework.http.client.exception.HttpClientException;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.retry.ExhaustedRetryException;
import io.basc.framework.retry.RetryOperations;

public interface DiscoveryLoadBalancer extends LoadBalancer<ServiceInstance> {
	Server<ServiceInstance> choose(String name, ServerAccept<ServiceInstance> accept);

	default <V, E extends Throwable> V execute(String name, RetryOperations retryOperations,
			LoadConsumer<ServiceInstance, V, E> consumer) throws E, ExhaustedRetryException {
		HashSet<String> errorSets = new HashSet<String>();
		return retryOperations.execute((context) -> {
			Server<ServiceInstance> server = choose(name, (s) -> {
				return !errorSets.contains(s.getId());
			});

			if (server == null) {
				try {
					return consumer.accept(context, server);
				} finally {
					context.setExhaustedOnly();
				}
			}

			try {
				return consumer.accept(context, server);
			} catch (HttpClientException e) {
				errorSets.add(server.getId());
				stat(server, State.FAILED);
				throw e;
			}
		});
	}

	default <V, E extends Throwable> V execute(URI uri, RetryOperations retryOperations, LoadUriConsumer<V, E> consumer)
			throws E, ExhaustedRetryException {
		return execute(uri.getHost(), retryOperations, (context, server) -> {
			if (server == null) {
				return consumer.accept(context, server, uri);
			}

			UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uri);
			builder = builder.host(server.getService().getHost());
			int port = builder.build().getPort();
			if (port == -1) {
				builder = builder.port(server.getService().getPort());
			}
			return consumer.accept(context, server, builder.build().toUri());
		});
	}
}
