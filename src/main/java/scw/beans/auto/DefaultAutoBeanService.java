package scw.beans.auto;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import scw.beans.BeanFactory;
import scw.beans.annotation.AutoImpl;
import scw.beans.annotation.Proxy;
import scw.core.PropertyFactory;
import scw.core.annotation.Host;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.rpc.http.HttpRestfulRpcProxy;
import scw.util.ExecutorUtils;

public final class DefaultAutoBeanService implements AutoBeanService {
	private static Logger logger = LoggerUtils.getLogger(DefaultAutoBeanService.class);

	private AutoBean defaultService(Class<?> clazz, BeanFactory beanFactory, PropertyFactory propertyFactory,
			AutoBeanServiceChain serviceChain) throws Exception {
		AutoBean autoBean = null;
		if (clazz == ExecutorService.class) {
			autoBean = createExecutorServiceAutoBean(beanFactory, propertyFactory);
		}

		if (autoBean != null) {
			return autoBean;
		}

		// 未注解service时接口默认实现
		if (clazz.isInterface()) {
			String name = clazz.getName() + "Impl";
			if (ClassUtils.isAvailable(name) && beanFactory.isInstance(name)) {
				logger.info("{} reference {}", clazz.getName(), name);
				return new ReferenceAutoBean(beanFactory, name);
			} else {
				int index = clazz.getName().lastIndexOf(".");
				name = index == -1 ? (clazz.getName() + "Impl")
						: (clazz.getName().substring(0, index) + ".impl." + clazz.getSimpleName() + "Impl");
				if (ClassUtils.isAvailable(name) && beanFactory.isInstance(name)) {
					logger.info("{} reference {}", clazz.getName(), name);
					return new ReferenceAutoBean(beanFactory, name);
				}
			}
		}

		if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
			// Host注解
			Host host = clazz.getAnnotation(Host.class);
			if (host != null) {
				String proxyName = propertyFactory.getProperty("rpc.http.host.proxy");
				if (StringUtils.isEmpty(proxyName)) {
					proxyName = HttpRestfulRpcProxy.class.getName();
				}

				if (beanFactory.isInstance(proxyName)) {
					return new ProxyAutoBean(beanFactory, clazz, Arrays.asList(proxyName));
				}
			}

			Proxy proxy = clazz.getAnnotation(Proxy.class);
			if (proxy != null) {
				return new ProxyAutoBean(beanFactory, clazz, AutoBeanUtils.getProxyNames(proxy));
			}
		}

		if (!ReflectionUtils.isInstance(clazz, false)) {
			return serviceChain.service(clazz, beanFactory, propertyFactory);
		}

		return new SimpleAutoBean(beanFactory, clazz, propertyFactory);
	}

	public AutoBean doService(Class<?> clazz, BeanFactory beanFactory, PropertyFactory propertyFactory,
			AutoBeanServiceChain serviceChain) throws Exception {
		AutoImpl autoConfig = clazz.getAnnotation(AutoImpl.class);
		if (autoConfig == null) {
			return defaultService(clazz, beanFactory, propertyFactory, serviceChain);
		}

		Collection<Class<?>> implList = AutoBeanUtils.getAutoImplClass(autoConfig, clazz, propertyFactory);
		if (CollectionUtils.isEmpty(implList)) {
			return defaultService(clazz, beanFactory, propertyFactory, serviceChain);
		}

		for (Class<?> clz : implList) {
			AutoBean autoBean = AutoBeanUtils.autoBeanService(clz, autoConfig, beanFactory, propertyFactory);
			if (autoBean != null && autoBean.isInstance()) {
				return autoBean;
			}
		}

		return defaultService(clazz, beanFactory, propertyFactory, serviceChain);
	}

	private AutoBean createExecutorServiceAutoBean(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		return new SingleInstanceAutoBean(beanFactory, ThreadPoolExecutor.class,
				ExecutorUtils.newExecutorService(true));
	}
}
