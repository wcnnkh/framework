package io.basc.framework.cloud.loadbalancer;

import java.net.CookieHandler;
import java.net.URI;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.client.ClientHttpRequestCallback;
import io.basc.framework.http.client.ClientHttpRequestFactory;
import io.basc.framework.http.client.ClientHttpResponseExtractor;
import io.basc.framework.http.client.DefaultHttpClient;
import io.basc.framework.http.client.HttpClientErrorException;
import io.basc.framework.http.client.RedirectManager;
import io.basc.framework.retry.RetryOperations;
import io.basc.framework.retry.support.RetryTemplate;
import io.basc.framework.util.Assert;

public class DiscoveryLoadBalancerHttpClient extends DefaultHttpClient {
	private final DiscoveryLoadBalancer loadbalancer;

	public DiscoveryLoadBalancerHttpClient(DiscoveryLoadBalancer loadbalancer) {
		this.loadbalancer = loadbalancer;
		setRetryOperations(new RetryTemplate());
	}

	public void setRetryOperations(RetryOperations retryOperations) {
		Assert.requiredArgument(retryOperations != null, "retryOperations");
		super.setRetryOperations(retryOperations);
	}

	@Override
	protected <T> HttpResponseEntity<T> execute(URI uri, String httpMethod, ClientHttpRequestFactory requestFactory,
			CookieHandler cookieHandler, ClientHttpRequestCallback requestCallback, RedirectManager redirectManager,
			ClientHttpResponseExtractor<T> responseExtractor, TypeDescriptor responseType) {
		return loadbalancer.execute(uri, getRetryOperations(), (context, server, serverUri) -> {
			try {
				return super.execute(serverUri, httpMethod, requestFactory, cookieHandler, requestCallback,
						redirectManager, responseExtractor, responseType);
			} catch (HttpClientErrorException e) {
				throw e;
			} catch (Throwable e) {
				// 其他异常不重试了
				context.setExhaustedOnly();
				throw e;
			}
		});
	}
}
