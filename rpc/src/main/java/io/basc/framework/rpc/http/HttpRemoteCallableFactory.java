package io.basc.framework.rpc.http;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;
import java.util.concurrent.Callable;

import io.basc.framework.cloud.loadbalancer.DiscoveryLoadBalancer;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.core.parameter.ParameterUtils;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.client.ClientHttpRequestFactory;
import io.basc.framework.lang.Nullable;
import io.basc.framework.retry.RetryOperations;
import io.basc.framework.retry.support.RetryTemplate;
import io.basc.framework.rpc.CallableFactory;
import io.basc.framework.web.message.WebMessageConverters;
import io.basc.framework.web.message.support.DefaultWebMessageConverters;
import io.basc.framework.web.pattern.DefaultHttpPatternResolvers;
import io.basc.framework.web.pattern.HttpPattern;
import io.basc.framework.web.pattern.HttpPatternResolvers;

public class HttpRemoteCallableFactory implements CallableFactory, Configurable {
	private RetryOperations retryOperations = new RetryTemplate();
	private final WebMessageConverters webMessageConverters;
	private final HttpPatternResolvers httpPatternResolvers = new DefaultHttpPatternResolvers();
	private final HttpRemoteResolver httpRemoteUriResolver;
	private final ClientHttpRequestFactory clientHttpRequestFactory;
	private final DiscoveryLoadBalancer discoveryLoadBalancer;

	public HttpRemoteCallableFactory(ClientHttpRequestFactory clientHttpRequestFactory,
			ConversionService conversionService, ParameterFactory defaultValueFactory,
			HttpRemoteResolver httpRemoteUriResolver, @Nullable DiscoveryLoadBalancer discoveryLoadBalancer) {
		this.webMessageConverters = new DefaultWebMessageConverters(conversionService, defaultValueFactory);
		this.clientHttpRequestFactory = clientHttpRequestFactory;
		this.httpRemoteUriResolver = httpRemoteUriResolver;
		this.discoveryLoadBalancer = discoveryLoadBalancer;
	}

	public WebMessageConverters getWebMessageConverters() {
		return webMessageConverters;
	}

	protected HttpPattern getHttpPattern(Class<?> clazz, Method method) {
		Collection<HttpPattern> patterns = null;
		if (httpPatternResolvers.canResolve(clazz, method)) {
			patterns = httpPatternResolvers.resolve(clazz, method);
		} else if (httpPatternResolvers.canResolve(method)) {
			patterns = httpPatternResolvers.resolve(method);
		}

		if (patterns == null) {
			return null;
		}

		HttpPattern httpPattern = patterns.stream().sorted().findFirst().orElse(null);
		if (httpPattern == null) {
			return null;
		}

		if (httpPattern.getMethod() == null) {
			return httpPattern.setMethod(HttpMethod.GET.name());
		}
		return httpPattern;
	}

	public HttpPatternResolvers getHttpPatternResolvers() {
		return httpPatternResolvers;
	}

	@Override
	public Callable<Object> getCallable(Class<?> clazz, Method method, Object[] args) {
		URI host = httpRemoteUriResolver.resolve(clazz, method);
		if (host == null) {
			return null;
		}

		HttpPattern httpPattern = getHttpPattern(clazz, method);
		if (httpPattern == null) {
			return null;
		}
		return new HttpRemoteCallable(webMessageConverters, clientHttpRequestFactory, host, httpPattern,
				ParameterUtils.getParameters(method), args, TypeDescriptor.forMethodReturnType(method), retryOperations,
				discoveryLoadBalancer);
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		httpPatternResolvers.configure(serviceLoaderFactory);
		webMessageConverters.configure(serviceLoaderFactory);
	}
}
