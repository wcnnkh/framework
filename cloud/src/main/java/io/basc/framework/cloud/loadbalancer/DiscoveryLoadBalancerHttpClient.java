package io.basc.framework.cloud.loadbalancer;

import java.net.URI;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.env.Sys;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.client.ClientHttpRequestCallback;
import io.basc.framework.http.client.ClientHttpResponseExtractor;
import io.basc.framework.http.client.DefaultHttpClient;
import io.basc.framework.http.client.exception.HttpClientException;
import io.basc.framework.retry.RetryOperations;
import io.basc.framework.retry.support.RetryTemplate;
import io.basc.framework.util.Assert;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
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
	public <T> HttpResponseEntity<T> execute(URI url, HttpMethod method, ClientHttpRequestCallback requestCallback,
			ClientHttpResponseExtractor<T> responseExtractor) throws HttpClientException {
		return loadbalancer.execute(url, getRetryOperations(), (context, server, uri) -> {
			return super.execute(uri, method, requestCallback, responseExtractor);
		});
	}
}
