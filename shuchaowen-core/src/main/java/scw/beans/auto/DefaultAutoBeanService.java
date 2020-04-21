package scw.beans.auto;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;

import scw.aop.Filter;
import scw.beans.AutoBeanBuilder;
import scw.beans.BeanBuilder;
import scw.beans.BeanFactory;
import scw.beans.ProxyBeanBuilder;
import scw.beans.ThreadPoolExecutorBeanBuilder;
import scw.beans.annotation.AutoImpl;
import scw.beans.annotation.Proxy;
import scw.core.instance.InstanceUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.PropertyFactory;

public final class DefaultAutoBeanService implements AutoBeanService {
	private static Logger logger = LoggerUtils
			.getLogger(DefaultAutoBeanService.class);

	private BeanBuilder defaultService(Class<?> clazz, BeanFactory beanFactory,
			PropertyFactory propertyFactory, AutoBeanServiceChain serviceChain)
			throws Exception {
		if (clazz == ExecutorService.class) {
			return new ThreadPoolExecutorBeanBuilder(beanFactory, propertyFactory);
		}

		// 未注解service时接口默认实现
		if (clazz.isInterface()) {
			String name = clazz.getName() + "Impl";
			if (ClassUtils.isPresent(name) && beanFactory.isInstance(name)) {
				logger.info("{} reference {}", clazz.getName(), name);
				return new AutoBeanBuilder(beanFactory, propertyFactory, ClassUtils.forName(name));
			} else {
				int index = clazz.getName().lastIndexOf(".");
				name = index == -1 ? (clazz.getName() + "Impl") : (clazz
						.getName().substring(0, index)
						+ ".impl."
						+ clazz.getSimpleName() + "Impl");
				if (ClassUtils.isPresent(name) && beanFactory.isInstance(name)) {
					logger.info("{} reference {}", clazz.getName(), name);
					return new AutoBeanBuilder(beanFactory, propertyFactory, ClassUtils.forName(name));
				}
			}
		}

		if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
			Proxy proxy = clazz.getAnnotation(Proxy.class);
			if (proxy != null) {
				return new ProxyBeanBuilder(beanFactory, propertyFactory, clazz, getProxyNames(proxy));
			}
		}

		if (!ReflectionUtils.isInstance(clazz, false)) {
			AutoBeanServiceChain autoBeanServiceChain = new NextAutoBeanServiceChain(
					InstanceUtils.getConfigurationClassList(
							AutoBeanService.class, propertyFactory),
					serviceChain);
			return autoBeanServiceChain.service(clazz, beanFactory,
					propertyFactory);
		}
		
		return new AutoBeanBuilder(beanFactory, propertyFactory, clazz);
	}

	public BeanBuilder doService(Class<?> clazz, BeanFactory beanFactory,
			PropertyFactory propertyFactory, AutoBeanServiceChain serviceChain)
			throws Exception {
		AutoImpl autoConfig = clazz.getAnnotation(AutoImpl.class);
		if (autoConfig == null) {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Collection<Class<?>> impls = InstanceUtils
					.getConfigurationClassList((Class) clazz, propertyFactory);
			if (!CollectionUtils.isEmpty(impls)) {
				for (Class<?> impl : impls) {
					return defaultService(impl, beanFactory, propertyFactory,
							serviceChain);
				}
			}
			return defaultService(clazz, beanFactory, propertyFactory,
					serviceChain);
		}

		Collection<Class<?>> implList = AutoBeanUtils.getAutoImplClass(
				autoConfig, clazz, propertyFactory);
		if (CollectionUtils.isEmpty(implList)) {
			return defaultService(clazz, beanFactory, propertyFactory,
					serviceChain);
		}

		for (Class<?> clz : implList) {
			BeanBuilder autoBean = AutoBeanUtils.autoBeanService(clz, autoConfig,
					beanFactory, propertyFactory);
			if (autoBean != null && autoBean.isInstance()) {
				return autoBean;
			}
		}

		return defaultService(clazz, beanFactory, propertyFactory, serviceChain);
	}

	private static class NextAutoBeanServiceChain extends
			AbstractAutoBeanServiceChain {
		private Iterator<Class<AutoBeanService>> iterator;

		public NextAutoBeanServiceChain(
				Collection<Class<AutoBeanService>> collection,
				AutoBeanServiceChain chain) {
			super(chain);
			if (!CollectionUtils.isEmpty(collection)) {
				this.iterator = collection.iterator();
			}
		}

		@Override
		protected AutoBeanService getNext(Class<?> clazz,
				BeanFactory beanFactory, PropertyFactory propertyFactory) {
			if (iterator == null) {
				return null;
			}

			if (iterator.hasNext()) {
				return beanFactory.getInstance(iterator.next());
			}

			return null;
		}
	}

	public static LinkedList<String> getProxyNames(Proxy proxy) {
		LinkedList<String> list = new LinkedList<String>();
		if (proxy == null) {
			return list;
		}

		for (String name : proxy.names()) {
			list.add(name);
		}

		for (Class<? extends Filter> c : proxy.value()) {
			list.add(c.getName());
		}

		return list;
	}
}
