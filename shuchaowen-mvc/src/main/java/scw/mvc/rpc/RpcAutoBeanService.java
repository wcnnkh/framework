package scw.mvc.rpc;

import java.util.Arrays;

import scw.beans.builder.BeanBuilder;
import scw.beans.builder.BeanBuilderLoader;
import scw.beans.builder.BeanBuilderLoaderChain;
import scw.beans.builder.LoaderContext;
import scw.beans.builder.ProxyBeanBuilder;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.StringUtils;
import scw.mvc.rpc.annotation.Host;
import scw.mvc.rpc.http.HttpRestfulRpcProxy;

@Configuration(order=Integer.MIN_VALUE)
public final class RpcAutoBeanService implements BeanBuilderLoader {

	public BeanBuilder loading(LoaderContext context,
			BeanBuilderLoaderChain serviceChain) {
		// Host注解
		Host host = context.getTargetClass().getAnnotation(Host.class);
		if (host != null) {
			String proxyName = context.getPropertyFactory().getString(
					"rpc.http.host.proxy");
			if (StringUtils.isEmpty(proxyName)) {
				proxyName = HttpRestfulRpcProxy.class.getName();
			}

			proxyName = context.getPropertyFactory().format(proxyName, true);
			if (context.getBeanFactory().isInstance(proxyName)) {
				return new ProxyBeanBuilder(context, Arrays.asList(proxyName));
			}
		}
		return serviceChain.loading(context);
	}
}
