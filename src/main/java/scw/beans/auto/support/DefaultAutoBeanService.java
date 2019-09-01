package scw.beans.auto.support;

import scw.beans.BeanFactory;
import scw.beans.auto.AutoBean;
import scw.beans.auto.AutoBeanService;
import scw.beans.auto.AutoBeanServiceChain;
import scw.beans.auto.DefaultAutoBean;
import scw.core.PropertyFactory;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.data.memcached.Memcached;

public final class DefaultAutoBeanService implements AutoBeanService {

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

		return serviceChain.service(clazz, beanFactory, propertyFactory);
	}

	private AutoBean createMemcached(BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		if (ClassUtils.isExist("scw.data.memcached.x.XMemcached")) {
			String host = propertyFactory.getProperty("memcached.hosts");
			if (!StringUtils.isEmpty(host)) {
				return new DefaultAutoBean(beanFactory,
						"scw.data.memcached.x.XMemcached",
						new Class<?>[] { String.class }, new Object[] { host });
			}
		}
		return null;
	}

}
