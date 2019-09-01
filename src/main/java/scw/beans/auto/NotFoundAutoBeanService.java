package scw.beans.auto;

import scw.beans.BeanFactory;
import scw.beans.annotation.AutoConfig;
import scw.core.PropertyFactory;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.data.memcached.Memcached;
import scw.logger.LazyLogger;
import scw.logger.Logger;

public final class NotFoundAutoBeanService implements AutoBeanService {
	private static Logger logger = new LazyLogger(NotFoundAutoBeanService.class);

	public AutoBean doService(Class<?> clazz, BeanFactory beanFactory,
			PropertyFactory propertyFactory, AutoBeanServiceChain serviceChain)
			throws Exception {
		AutoBean autoBean = null;
		if (clazz == Memcached.class) {
			autoBean = createMemcached(beanFactory, propertyFactory);
		}

		if (autoBean != null) {
			return autoBean;
		}

		AutoConfig autoConfig = clazz.getAnnotation(AutoConfig.class);
		if (autoConfig == null || autoConfig.service() == Object.class
				|| autoConfig.service() == clazz) {
			if (!ReflectUtils.isInstance(clazz, false)) {
				return serviceChain
						.service(clazz, beanFactory, propertyFactory);
			}

			return new DefaultAutoBean(beanFactory, clazz);
		}

		return AutoBeanUtils.autoBeanService(autoConfig.service(), autoConfig,
				beanFactory, propertyFactory);
	}

	private AutoBean createMemcached(BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		if (ClassUtils.isExist("scw.data.memcached.x.XMemcached")) {
			String host = propertyFactory.getProperty("memcached.hosts");
			if (!StringUtils.isEmpty(host)) {
				logger.info("using default memcached config:{}", host);
				return new DefaultAutoBean(beanFactory,
						"scw.data.memcached.x.XMemcached",
						new Class<?>[] { String.class }, new Object[] { host });
			}
		}
		return null;
	}
}
