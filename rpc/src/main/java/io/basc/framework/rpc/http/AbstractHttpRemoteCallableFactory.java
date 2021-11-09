package io.basc.framework.rpc.http;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;
import java.util.concurrent.Callable;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.BeanFactoryAware;
import io.basc.framework.cloud.loadbalancer.DiscoveryLoadBalancer;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterUtils;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ServiceLoaderFactory;
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

public abstract class AbstractHttpRemoteCallableFactory implements CallableFactory, Configurable, BeanFactoryAware {
	private RetryOperations retryOperations = new RetryTemplate();
	private final WebMessageConverters webMessageConverters = new WebMessageConverters();
	private final HttpPatternResolvers httpPatternResolvers = new DefaultHttpPatternResolvers();
	private final ClientHttpRequestFactory clientHttpRequestFactory;
	private final DiscoveryLoadBalancer discoveryLoadBalancer;

	public AbstractHttpRemoteCallableFactory(ClientHttpRequestFactory clientHttpRequestFactory,
			@Nullable DiscoveryLoadBalancer discoveryLoadBalancer) {
		this.clientHttpRequestFactory = clientHttpRequestFactory;
		this.discoveryLoadBalancer = discoveryLoadBalancer;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) {
		WebMessageConverters webMessageConverters = new DefaultWebMessageConverters(
				beanFactory.getEnvironment().getConversionService(), beanFactory.getDefaultValueFactory());
		this.webMessageConverters.setAfterService(webMessageConverters);
	}

	public WebMessageConverters getWebMessageConverters() {
		return webMessageConverters;
	}

	protected abstract URI getHost(Class<?> clazz, Method method);

	protected HttpPattern getHttpPattern(Class<?> clazz, Method method) {
		if (!httpPatternResolvers.canResolve(method)) {
			return null;
		}

		Collection<HttpPattern> patterns = httpPatternResolvers.resolve(method);
		if (patterns == null) {
			return null;
		}

		return patterns.stream().sorted().findFirst().orElse(null);
	}

	@Override
	public Callable<Object> getCallable(Class<?> clazz, Method method, Object[] args) {
		URI host = getHost(clazz, method);
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
	}

}
