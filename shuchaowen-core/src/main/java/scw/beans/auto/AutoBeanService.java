package scw.beans.auto;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;

public interface AutoBeanService {
	AutoBean doService(Class<?> clazz, BeanFactory beanFactory,
			PropertyFactory propertyFactory, AutoBeanServiceChain serviceChain) throws Exception;
}
