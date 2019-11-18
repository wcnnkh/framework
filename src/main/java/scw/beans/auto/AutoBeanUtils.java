package scw.beans.auto;

import java.util.Collection;
import java.util.LinkedList;

import scw.aop.Filter;
import scw.beans.BeanFactory;
import scw.beans.annotation.AutoImpl;
import scw.beans.annotation.Proxy;
import scw.core.PropertyFactory;
import scw.core.exception.BeansException;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.FormatUtils;
import scw.core.utils.StringUtils;

public final class AutoBeanUtils {
	private static final AutoBeanService DEFAULT_AUTO_BEAN_SERVICE = new DefaultAutoBeanService();
	private static final LinkedList<Object> services = new LinkedList<Object>();

	private AutoBeanUtils() {
	};

	private static Collection<AutoBeanService> getAutoBeanServices(AutoImpl autoConfig, BeanFactory beanFactory,
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

					autoBeanServices.add((AutoBeanService) beanFactory.getInstance(name));
				}
			}
		}

		for (Object service : services) {
			if (service == null) {
				continue;
			}

			if (service instanceof AutoBeanService) {
				autoBeanServices.add((AutoBeanService) service);
			} else {
				autoBeanServices.add((AutoBeanService) beanFactory.getInstance(service.toString()));
			}
		}

		if (autoConfig != null) {
			for (String name : autoConfig.serviceName()) {
				if (StringUtils.isEmpty(name)) {
					continue;
				}

				name = FormatUtils.format(name, propertyFactory, true);
				autoBeanServices.add((AutoBeanService) beanFactory.getInstance(name));
			}

			for (Class<? extends AutoBeanService> service : autoConfig.service()) {
				if (service == null) {
					continue;
				}

				autoBeanServices.add(beanFactory.getInstance(service));
			}
		}

		autoBeanServices.add(DEFAULT_AUTO_BEAN_SERVICE);
		return autoBeanServices;
	}

	public static void addAutoBeanService(String name) {
		synchronized (services) {
			services.add(name);
		}
	}

	public static void addAutoBeanService(AutoBeanService autoBeanService) {
		synchronized (services) {
			services.add(autoBeanService);
		}
	}

	public static AutoBean autoBeanService(Class<?> clazz, AutoImpl autoConfig, BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		Collection<AutoBeanService> autoBeanServices = AutoBeanUtils.getAutoBeanServices(autoConfig, beanFactory,
				propertyFactory);
		if (!CollectionUtils.isEmpty(autoBeanServices)) {
			AutoBeanServiceChain serviceChain = new SimpleAutoBeanServiceChain(autoBeanServices);
			try {
				return serviceChain.service(clazz, beanFactory, propertyFactory);
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
}
