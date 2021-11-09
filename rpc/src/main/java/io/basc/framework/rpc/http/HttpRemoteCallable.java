package io.basc.framework.rpc.http;

import java.net.URI;
import java.util.concurrent.Callable;

import io.basc.framework.cloud.loadbalancer.DiscoveryLoadBalancer;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.http.client.ClientHttpRequestFactory;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.uri.UriComponentsBuilder;
import io.basc.framework.retry.RetryOperations;
import io.basc.framework.web.message.WebMessageConverter;
import io.basc.framework.web.pattern.HttpPattern;

final class HttpRemoteCallable implements Callable<Object> {
	private final WebMessageConverter webMessageConverter;
	private final ClientHttpRequestFactory clientHttpRequestFactory;
	private final HttpPattern httpPattern;
	private final ParameterDescriptor[] parameterDescriptors;
	private final Object[] args;
	private final TypeDescriptor returnType;
	private final URI host;
	private final RetryOperations retryOperations;
	private final DiscoveryLoadBalancer discoveryLoadBalancer;

	public HttpRemoteCallable(WebMessageConverter webMessageConverter,
			ClientHttpRequestFactory clientHttpRequestFactory, URI host, HttpPattern httpPattern,
			ParameterDescriptor[] parameterDescriptors, Object[] args, TypeDescriptor returnType,
			RetryOperations retryOperations, @Nullable DiscoveryLoadBalancer discoveryLoadBalancer) {
		this.webMessageConverter = webMessageConverter;
		this.host = host;
		this.clientHttpRequestFactory = clientHttpRequestFactory;
		this.httpPattern = httpPattern;
		this.parameterDescriptors = parameterDescriptors;
		this.args = args;
		this.returnType = returnType;
		this.retryOperations = retryOperations;
		this.discoveryLoadBalancer = discoveryLoadBalancer;
	}

	@Override
	public Object call() throws Exception {
		if (discoveryLoadBalancer == null) {
			return call(host);
		}
		return discoveryLoadBalancer.execute(host, retryOperations, (context, server, uri) -> call(uri));
	}

	private Object call(URI host) throws Exception {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUri(host).path(httpPattern.getPath());
		if (parameterDescriptors != null) {
			int i = 0;
			for (ParameterDescriptor parameterDescriptor : parameterDescriptors) {
				builder = webMessageConverter.write(builder, parameterDescriptor, args[i++]);
			}
		}

		URI uri = builder.build().toUri();
		ClientHttpRequest request = clientHttpRequestFactory.createRequest(uri, httpPattern.getMethod());
		if (httpPattern.hasConsumes()) {
			request.getHeaders().put(HttpHeaders.CONTENT_TYPE, httpPattern.getConsumes().getRawMimeTypes());
		}

		if (parameterDescriptors != null) {
			int i = 0;
			for (ParameterDescriptor parameterDescriptor : parameterDescriptors) {
				request = webMessageConverter.write(request, parameterDescriptor, args[i]);
			}
		}

		ClientHttpResponse response = request.execute();
		try {
			return webMessageConverter.read(response, returnType);
		} finally {
			response.close();
		}
	}
}
