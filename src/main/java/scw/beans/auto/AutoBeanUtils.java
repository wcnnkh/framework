package scw.beans.auto;

import java.util.Collection;
import java.util.LinkedList;

import scw.beans.BeanFactory;
import scw.beans.annotation.AutoConfig;
import scw.core.PropertyFactory;
import scw.core.exception.BeansException;
import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;

public final class AutoBeanUtils {
	private static final AutoBeanService DEFAULT_AUTO_BEAN_SERVICE = new DefaultAutoBeanService();
	private static final LinkedList<Object> services = new LinkedList<Object>();

	private AutoBeanUtils() {
	};

	public static Collection<AutoBeanService> getAutoBeanServices(AutoConfig autoConfig, BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		LinkedList<AutoBeanService> autoBeanServices = new LinkedList<AutoBeanService>();
		String value = SystemPropertyUtils.getProperty("beans.auto.names");
		if (!StringUtils.isEmpty(value)) {
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
			for (String name : autoConfig.autoBeanServiceNames()) {
				if (StringUtils.isEmpty(name)) {
					continue;
				}

				autoBeanServices.add((AutoBeanService) beanFactory.getInstance(name));
			}

			for (Class<? extends AutoBeanService> service : autoConfig.autoBeanServices()) {
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

	public static AutoBean autoBeanService(Class<?> clazz, AutoConfig autoConfig, BeanFactory beanFactory,
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
}
