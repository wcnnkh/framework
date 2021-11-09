package io.basc.framework.rpc.http.annotation;

import java.lang.reflect.Method;
import java.net.URI;

import io.basc.framework.cloud.loadbalancer.DiscoveryLoadBalancer;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.core.annotation.AnnotationUtils;
import io.basc.framework.core.parameter.ParameterFactory;
import io.basc.framework.env.Environment;
import io.basc.framework.env.EnvironmentAware;
import io.basc.framework.http.client.ClientHttpRequestFactory;
import io.basc.framework.net.uri.UriUtils;
import io.basc.framework.rpc.http.AbstractHttpRemoteCallableFactory;

public class AnnotationHttpRemoteCallableFactory extends AbstractHttpRemoteCallableFactory implements EnvironmentAware {
	private Environment environment;

	public AnnotationHttpRemoteCallableFactory(ClientHttpRequestFactory clientHttpRequestFactory,
			ConversionService conversionService, ParameterFactory defaultValueFactory,
			DiscoveryLoadBalancer discoveryLoadBalancer) {
		super(clientHttpRequestFactory, conversionService, defaultValueFactory, discoveryLoadBalancer);
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	protected URI getHost(Class<?> clazz, Method method) {
		HttpRemote httpRemote = AnnotationUtils.getAnnotation(HttpRemote.class, clazz, method);
		if (httpRemote == null) {
			return null;
		}

		String url = httpRemote.value();
		if (environment != null) {
			url = environment.resolvePlaceholders(url);
		}
		return UriUtils.toUri(url);
	}

}
