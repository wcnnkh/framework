package io.basc.framework.cloud.loadbalancer;

import java.net.URI;
import java.util.HashSet;

import io.basc.framework.cloud.ServiceInstance;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.env.Sys;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.client.ClientHttpRequestCallback;
import io.basc.framework.http.client.ClientHttpRequestFactory;
import io.basc.framework.http.client.ClientHttpResponseExtractor;
import io.basc.framework.http.client.DefaultHttpClient;
import io.basc.framework.http.client.HttpClient;
import io.basc.framework.http.client.exception.HttpClientException;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.retry.RetryOperations;
import io.basc.framework.retry.support.RetryTemplate;
import io.basc.framework.util.Assert;

@Provider(order = Ordered.LOWEST_PRECEDENCE, value = HttpClient.class)
public class DiscoveryLoadBalancerHttpClient extends DefaultHttpClient {
	private final DiscoveryLoadBalancer loadbalancer;
	private RetryOperations retryOperations = Sys.env.getServiceLoader(RetryOperations.class)
			.first(() -> new RetryTemplate());

	public DiscoveryLoadBalancerHttpClient(DiscoveryLoadBalancer loadbalancer) {
		this.loadbalancer = loadbalancer;
	}

	public RetryOperations getRetryOperations() {
		return retryOperations;
	}

	public void setRetryOperations(RetryOperations retryOperations) {
		Assert.requiredArgument(retryOperations != null, "retryOperations");
		this.retryOperations = retryOperations;
	}

	@Override
	public <T> HttpResponseEntity<T> execute(URI url, HttpMethod method, ClientHttpRequestFactory requestFactory,
			ClientHttpRequestCallback requestCallback, ClientHttpResponseExtractor<T> responseExtractor)
			throws HttpClientException {
		String host = url.getHost();
		final HashSet<String> errorSets = new HashSet<String>();
		return getRetryOperations().execute((context) -> {
			Server<ServiceInstance> server = loadbalancer.choose(host, (s) -> {
				return !errorSets.contains(s.getId());
			});
			
			if (server == null) {
				try {
					return super.execute(url, method, requestFactory, requestCallback, responseExtractor);
				} finally {
					context.setExhaustedOnly();
				}
			}

			UriComponentsBuilder builder = UriComponentsBuilder.fromUri(url);
			builder = builder.host(server.getService().getHost());
			int port = builder.build().getPort();
			if (port == -1) {
				builder = builder.port(server.getService().getPort());
			}

			try {
				return super.execute(builder.build().toUri(), method, requestFactory, requestCallback,
						responseExtractor);
			} catch (Throwable e) {
				errorSets.add(server.getId());
				loadbalancer.stat(server, State.FAILED);
				throw e;
			}
		});
	}
}
