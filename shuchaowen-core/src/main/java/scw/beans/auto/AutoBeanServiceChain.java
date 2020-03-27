package scw.beans.auto;

import scw.beans.BeanFactory;
import scw.util.value.property.PropertyFactory;

public interface AutoBeanServiceChain {
	AutoBean service(Class<?> clazz, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception;
}
