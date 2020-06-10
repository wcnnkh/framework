package scw.rpc.http;

import java.util.Arrays;

import scw.aop.MultiFilter;
import scw.beans.builder.BeanBuilder;
import scw.beans.builder.BeanBuilderLoader;
import scw.beans.builder.BeanBuilderLoaderChain;
import scw.beans.builder.LoaderContext;
import scw.beans.builder.ProxyBeanBuilder;
import scw.core.instance.annotation.Configuration;
import scw.rpc.http.annotation.HttpClient;

@Configuration(order = Integer.MIN_VALUE)
public final class HttpRpcBeanBuilderLoader implements BeanBuilderLoader {

	public BeanBuilder loading(LoaderContext context,
			BeanBuilderLoaderChain serviceChain) {
		// Host注解
		HttpClient httpClient = context.getTargetClass().getAnnotation(
				HttpClient.class);
		if (httpClient != null) {
			String proxyName = HttpRpcProxyFilter.class.getName();
			if (context.getBeanFactory().isInstance(proxyName)) {
				return new ProxyBeanBuilder(context, new MultiFilter(
						context.getBeanFactory(), Arrays.asList(proxyName)));
			}
		}
		return serviceChain.loading(context);
	}
}
