package io.basc.framework.rpc.http;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;
import java.util.concurrent.Callable;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterUtils;
import io.basc.framework.env.Environment;
import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.client.HttpClient;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.rpc.CallableFactory;
import io.basc.framework.web.message.WebMessageConverters;
import io.basc.framework.web.message.support.DefaultWebMessageConverters;
import io.basc.framework.web.pattern.DefaultHttpPatternResolvers;
import io.basc.framework.web.pattern.HttpPattern;
import io.basc.framework.web.pattern.HttpPatternResolvers;

public class HttpRemoteCallableFactory implements CallableFactory, Configurable {
	private static Logger logger = LoggerFactory.getLogger(HttpRemoteCallableFactory.class);

	private final WebMessageConverters webMessageConverters;
	private final HttpPatternResolvers httpPatternResolvers = new DefaultHttpPatternResolvers();
	private final HttpRemoteResolver httpRemoteUriResolver;
	private final HttpClient httpClient;
	private final Environment environment;

	public HttpRemoteCallableFactory(HttpClient httpClient, HttpRemoteResolver httpRemoteUriResolver,
			Environment environment) {
		this.webMessageConverters = new DefaultWebMessageConverters(environment);
		this.httpClient = httpClient;
		this.httpRemoteUriResolver = httpRemoteUriResolver;
		this.environment = environment;
	}

	public Environment getEnvironment() {
		return environment;
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
		URI host = httpRemoteUriResolver.resolve(clazz, method, environment);
		if (host == null) {
			return null;
		}

		HttpPattern httpPattern = getHttpPattern(clazz, method);
		if (httpPattern == null) {
			return null;
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Create {} callable by host: {}", method, host);
		}

		return new HttpRemoteCallable(webMessageConverters, httpClient, host, httpPattern,
				ParameterUtils.getParameters(method), args, TypeDescriptor.forMethodReturnType(method));
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		httpPatternResolvers.configure(serviceLoaderFactory);
		webMessageConverters.configure(serviceLoaderFactory);
	}
}
