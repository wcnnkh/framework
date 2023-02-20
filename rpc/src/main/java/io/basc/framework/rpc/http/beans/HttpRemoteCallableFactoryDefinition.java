package io.basc.framework.rpc.http.beans;

import io.basc.framework.env.Environment;
import io.basc.framework.env.EnvironmentBeanDefinition;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.http.HttpUtils;
import io.basc.framework.http.client.HttpClient;
import io.basc.framework.rpc.http.HttpRemoteCallableFactory;
import io.basc.framework.rpc.http.HttpRemoteResolver;

class HttpRemoteCallableFactoryDefinition extends EnvironmentBeanDefinition {

	public HttpRemoteCallableFactoryDefinition(Environment environment) {
		super(environment, HttpRemoteCallableFactory.class);
	}

	@Override
	public Object create() throws InstanceException {
		HttpClient httpClient = getBeanFactory().isInstance(HttpClient.class)
				? getBeanFactory().getInstance(HttpClient.class)
				: HttpUtils.getClient();
		HttpRemoteResolver httpRemoteUriResolver = getBeanFactory().getInstance(HttpRemoteResolver.class);
		return new HttpRemoteCallableFactory(httpClient, httpRemoteUriResolver, getEnvironment());
	}
}
