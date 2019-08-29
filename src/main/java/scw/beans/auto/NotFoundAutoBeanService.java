package scw.beans.auto;

import scw.beans.BeanFactory;
import scw.beans.annotation.AutoConfig;
import scw.core.PropertyFactory;
import scw.core.reflect.ReflectUtils;

public final class NotFoundAutoBeanService implements AutoBeanService {

	public AutoBean doService(Class<?> clazz, BeanFactory beanFactory, PropertyFactory propertyFactory,
			AutoBeanServiceChain serviceChain) throws Exception {
		AutoConfig autoConfig = clazz.getAnnotation(AutoConfig.class);
		if (autoConfig == null || autoConfig.service() == Object.class || autoConfig.service() == clazz) {
			if (!ReflectUtils.isInstance(clazz, false)) {
				return serviceChain.service(clazz, beanFactory, propertyFactory);
			}

			return new DefaultAutoBean(beanFactory, clazz);
		}

		return AutoBeanUtils.autoBeanService(autoConfig.service(), autoConfig, beanFactory, propertyFactory);
	}

}
