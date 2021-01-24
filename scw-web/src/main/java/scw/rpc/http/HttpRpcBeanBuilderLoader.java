package scw.rpc.http;

import java.util.Arrays;

import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionLoader;
import scw.beans.BeanDefinitionLoaderChain;
import scw.beans.BeanFactory;
import scw.beans.support.ProxyBeanDefinition;
import scw.context.annotation.Provider;
import scw.rpc.http.annotation.HttpClient;

@Provider(order = Integer.MIN_VALUE)
public final class HttpRpcBeanBuilderLoader implements BeanDefinitionLoader {

	public BeanDefinition load(BeanFactory beanFactory, Class<?> sourceClass,
			BeanDefinitionLoaderChain serviceChain) {
		// Host注解
		HttpClient httpClient = sourceClass.getAnnotation(
				HttpClient.class);
		if (httpClient != null) {
			String proxyName = HttpRpcProxyMethodInterceptor.class.getName();
			if (beanFactory.isInstance(proxyName)) {
				return new ProxyBeanDefinition(beanFactory, sourceClass, Arrays.asList(proxyName));
			}
		}
		return serviceChain.load(beanFactory, sourceClass);
	}
}
