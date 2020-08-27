package scw.rpc.http;

import java.util.Arrays;

import scw.beans.BeanDefinition;
import scw.beans.builder.BeanBuilderLoader;
import scw.beans.builder.BeanBuilderLoaderChain;
import scw.beans.builder.LoaderContext;
import scw.beans.builder.ProxyBeanDefinition;
import scw.core.instance.annotation.Configuration;
import scw.rpc.http.annotation.HttpClient;

@Configuration(order = Integer.MIN_VALUE)
public final class HttpRpcBeanBuilderLoader implements BeanBuilderLoader {

	public BeanDefinition loading(LoaderContext context,
			BeanBuilderLoaderChain serviceChain) {
		// Host注解
		HttpClient httpClient = context.getTargetClass().getAnnotation(
				HttpClient.class);
		if (httpClient != null) {
			String proxyName = HttpRpcProxyMethodInterceptor.class.getName();
			if (context.getBeanFactory().isInstance(proxyName)) {
				return new ProxyBeanDefinition(context, Arrays.asList(proxyName));
			}
		}
		return serviceChain.loading(context);
	}
}
