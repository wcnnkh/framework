package scw.rpc.http;

import scw.aop.MethodInterceptor;
import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionLoader;
import scw.beans.BeanDefinitionLoaderChain;
import scw.beans.BeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.context.annotation.Provider;
import scw.rpc.http.annotation.HttpClient;

@Provider
public final class HttpRpcBeanDefinitionLoader implements BeanDefinitionLoader {

	public BeanDefinition load(BeanFactory beanFactory, Class<?> sourceClass,
			BeanDefinitionLoaderChain serviceChain) {
		// Host注解
		HttpClient httpClient = sourceClass.getAnnotation(
				HttpClient.class);
		if (httpClient != null) {
			String proxyName = HttpRpcProxyMethodInterceptor.class.getName();
			if (beanFactory.isInstance(proxyName)) {
				DefaultBeanDefinition definition = new DefaultBeanDefinition(beanFactory, sourceClass);
				definition.getMethodInterceptors().addMethodInterceptor((MethodInterceptor)beanFactory.getInstance(proxyName));
			}
		}
		return serviceChain.load(beanFactory, sourceClass);
	}
}
