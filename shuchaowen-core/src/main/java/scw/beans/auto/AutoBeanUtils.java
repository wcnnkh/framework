package scw.beans.auto;

import java.util.Collection;
import java.util.LinkedList;

import scw.aop.Filter;
import scw.beans.BeanFactory;
import scw.beans.BeansException;
import scw.beans.annotation.AutoImpl;
import scw.beans.annotation.Proxy;
import scw.core.PropertyFactory;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.FormatUtils;

public final class AutoBeanUtils {
	private static Logger logger = LoggerUtils.getLogger(AutoBeanUtils.class);

	private AutoBeanUtils() {
	};

	private static Collection<AutoBeanService> getAutoBeanServices(
			AutoImpl autoConfig, BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		LinkedList<AutoBeanService> autoBeanServices = new LinkedList<AutoBeanService>();
		String value = propertyFactory.getProperty("beans.auto.names");
		if (!StringUtils.isEmpty(value)) {
			value = FormatUtils.format(value, propertyFactory, true);
			String[] names = StringUtils.commonSplit(value);
			if (!ArrayUtils.isEmpty(names)) {
				for (String name : names) {
					if (StringUtils.isEmpty(name)) {
						continue;
					}

					autoBeanServices.add((AutoBeanService) beanFactory
							.getInstance(name));
				}
			}
		}

		if (autoConfig != null) {
			for (String name : autoConfig.serviceName()) {
				if (StringUtils.isEmpty(name)) {
					continue;
				}

				name = FormatUtils.format(name, propertyFactory, true);
				autoBeanServices.add((AutoBeanService) beanFactory
						.getInstance(name));
			}

			for (Class<? extends AutoBeanService> service : autoConfig
					.service()) {
				if (service == null) {
					continue;
				}

				autoBeanServices.add(beanFactory.getInstance(service));
			}
		}

		autoBeanServices.add(new DefaultAutoBeanService());
		return autoBeanServices;
	}

	public static AutoBean autoBeanService(Class<?> clazz, AutoImpl autoConfig,
			BeanFactory beanFactory, PropertyFactory propertyFactory) {
		Collection<AutoBeanService> autoBeanServices = AutoBeanUtils
				.getAutoBeanServices(autoConfig, beanFactory, propertyFactory);
		if (!CollectionUtils.isEmpty(autoBeanServices)) {
			AutoBeanServiceChain serviceChain = new SimpleAutoBeanServiceChain(
					autoBeanServices, null);
			try {
				return serviceChain
						.service(clazz, beanFactory, propertyFactory);
			} catch (Exception e) {
				throw new BeansException(clazz.getName(), e);
			}
		}
		return null;
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

	public static Collection<Class<?>> getAutoImplClass(AutoImpl autoConfig,
			Class<?> type, PropertyFactory propertyFactory) {
		LinkedList<Class<?>> list = new LinkedList<Class<?>>();
		for (String name : autoConfig.className()) {
			if (StringUtils.isEmpty(name)) {
				continue;
			}

			name = FormatUtils.format(name, propertyFactory, true);
			if (!ClassUtils.isPresent(name)) {
				continue;
			}

			Class<?> clz = null;
			try {
				clz = ClassUtils.forName(name);
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
