package scw.beans.auto;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;
import scw.core.reflect.ReflectUtils;

public class DefaultAutoBeanService implements AutoBeanService {

	public AutoBean doService(Class<?> clazz, BeanFactory beanFactory,
			PropertyFactory propertyFactory, AutoBeanServiceChain serviceChain)
			throws Exception {
		if (!ReflectUtils.isInstance(clazz, false)) {
			return serviceChain.service(clazz, beanFactory, propertyFactory);
		}

		return new DefaultAutoBean(beanFactory, clazz);
	}

}
