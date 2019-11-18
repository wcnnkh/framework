package scw.beans.auto;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Executor;

import scw.beans.BeanFactory;
import scw.beans.annotation.AutoImpl;
import scw.core.PropertyFactory;
import scw.core.annotation.Host;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.DefaultExecutor;
import scw.core.utils.FormatUtils;
import scw.core.utils.StringUtils;
import scw.logger.LazyLogger;
import scw.logger.Logger;
import scw.rpc.http.HttpRestfulRpcProxy;

public final class DefaultAutoBeanService implements AutoBeanService {
	private static Logger logger = new LazyLogger(DefaultAutoBeanService.class);

	private AutoBean defaultService(Class<?> clazz, BeanFactory beanFactory, PropertyFactory propertyFactory,
			AutoBeanServiceChain serviceChain) throws Exception {
		AutoBean autoBean = null;
		if (clazz == Executor.class) {
			autoBean = new SimpleAutoBean(beanFactory, DefaultExecutor.class, propertyFactory);
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
					return new ProxyAutoBean(beanFactory, clazz, proxyName);
				}
			}
		}

		if (!ReflectUtils.isInstance(clazz, false)) {
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

		Collection<Class<?>> implList = getAutoImplClass(autoConfig, clazz, propertyFactory);
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

	private static Collection<Class<?>> getAutoImplClass(AutoImpl autoConfig, Class<?> type,
			PropertyFactory propertyFactory) {
		LinkedList<Class<?>> list = new LinkedList<Class<?>>();
		for (String name : autoConfig.className()) {
			if (StringUtils.isEmpty(name)) {
				continue;
			}

			name = FormatUtils.format(name, propertyFactory, true);
			if (!ClassUtils.isAvailable(name)) {
				continue;
			}

			Class<?> clz = null;
			try {
				clz = Class.forName(name);
			} catch (ClassNotFoundException e) {
			} catch (NoClassDefFoundError e) {
			}

			if (clz == null) {
				continue;
			}

			if (type.isAssignableFrom(clz)) {
				list.add(clz);
			} else {
				logger.warn("{} not is assignable from name {}", type, clz);
			}
		}

		for (Class<?> clz : autoConfig.value()) {
			if (type.isAssignableFrom(clz)) {
				list.add(clz);
			} else {
				logger.warn("{} not is assignable from {}", type, clz);
			}
		}
		return list;
	}
}
