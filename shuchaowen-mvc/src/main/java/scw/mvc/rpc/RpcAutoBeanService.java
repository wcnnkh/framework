package scw.mvc.rpc;

import java.util.Arrays;

import scw.beans.BeanFactory;
import scw.beans.auto.AutoBean;
import scw.beans.auto.AutoBeanService;
import scw.beans.auto.AutoBeanServiceChain;
import scw.beans.auto.ProxyAutoBean;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.StringUtils;
import scw.mvc.rpc.annotation.Host;
import scw.mvc.rpc.http.HttpRestfulRpcProxy;
import scw.util.value.property.PropertyFactory;

@Configuration
public final class RpcAutoBeanService implements AutoBeanService {

	public AutoBean doService(Class<?> clazz, BeanFactory beanFactory, PropertyFactory propertyFactory,
			AutoBeanServiceChain serviceChain) throws Exception {
		// Host注解
		Host host = clazz.getAnnotation(Host.class);
		if (host != null) {
			String proxyName = propertyFactory.getString("rpc.http.host.proxy");
			if (StringUtils.isEmpty(proxyName)) {
				proxyName = HttpRestfulRpcProxy.class.getName();
			}

			if (beanFactory.isInstance(proxyName)) {
				return new ProxyAutoBean(beanFactory, clazz, Arrays.asList(proxyName));
			}
		}
		return serviceChain.service(clazz, beanFactory, propertyFactory);
	}
}
