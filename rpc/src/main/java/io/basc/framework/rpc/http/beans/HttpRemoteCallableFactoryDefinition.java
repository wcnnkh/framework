package io.basc.framework.rpc.http.beans;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.cloud.loadbalancer.DiscoveryLoadBalancer;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.http.client.ClientHttpRequestFactory;
import io.basc.framework.http.client.DefaultHttpClient;
import io.basc.framework.rpc.http.HttpRemoteCallableFactory;
import io.basc.framework.rpc.http.HttpRemoteResolver;

class HttpRemoteCallableFactoryDefinition extends DefaultBeanDefinition {

	public HttpRemoteCallableFactoryDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, HttpRemoteCallableFactory.class);
	}

	@Override
	public Object create() throws InstanceException {
		ClientHttpRequestFactory clientHttpRequestFactory = beanFactory.isInstance(ClientHttpRequestFactory.class)
				? beanFactory.getInstance(ClientHttpRequestFactory.class)
				: DefaultHttpClient.CLIENT_HTTP_REQUEST_FACTORY;
		HttpRemoteResolver httpRemoteUriResolver = beanFactory.getInstance(HttpRemoteResolver.class);
		DiscoveryLoadBalancer discoveryLoadBalancer = beanFactory.isInstance(DiscoveryLoadBalancer.class)
				? beanFactory.getInstance(DiscoveryLoadBalancer.class)
				: null;
		return new HttpRemoteCallableFactory(clientHttpRequestFactory,
				beanFactory.getEnvironment().getConversionService(), getDefaultValueFactory(), httpRemoteUriResolver,
				beanFactory.getEnvironment(), discoveryLoadBalancer);
	}
}
