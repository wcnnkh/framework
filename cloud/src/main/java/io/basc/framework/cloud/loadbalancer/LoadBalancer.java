package io.basc.framework.cloud.loadbalancer;

import java.util.HashSet;
import java.util.function.Predicate;

import io.basc.framework.http.client.HttpClientException;
import io.basc.framework.lang.Nullable;
import io.basc.framework.retry.ExhaustedRetryException;
import io.basc.framework.retry.RetryOperations;

public interface LoadBalancer<T> {
	@Nullable
	Server<T> choose(Predicate<Server<T>> accept);

	void stat(Server<T> server, State state);

	default <V, E extends Throwable> V execute(RetryOperations retryOperations, LoadConsumer<T, V, E> consumer)
			throws E, ExhaustedRetryException {
		HashSet<String> errorSets = new HashSet<String>();
		return retryOperations.execute((context) -> {
			Server<T> server = choose((s) -> {
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
}
