package io.basc.framework.rpc.http.beans;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.http.HttpUtils;
import io.basc.framework.http.client.HttpClient;
import io.basc.framework.rpc.http.HttpRemoteCallableFactory;
import io.basc.framework.rpc.http.HttpRemoteResolver;

class HttpRemoteCallableFactoryDefinition extends DefaultBeanDefinition {

	public HttpRemoteCallableFactoryDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, HttpRemoteCallableFactory.class);
	}

	@Override
	public Object create() throws InstanceException {
		HttpClient httpClient = beanFactory.isInstance(HttpClient.class) ? beanFactory.getInstance(HttpClient.class)
				: HttpUtils.getHttpClient();
		HttpRemoteResolver httpRemoteUriResolver = beanFactory.getInstance(HttpRemoteResolver.class);
		return new HttpRemoteCallableFactory(httpClient, beanFactory.getEnvironment().getConversionService(),
				getDefaultValueFactory(), httpRemoteUriResolver, beanFactory.getEnvironment());
	}
}
