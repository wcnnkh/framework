package scw.beans.auto;

import scw.beans.BeanBuilder;
import scw.beans.BeanFactory;
import scw.util.value.property.PropertyFactory;

public interface AutoBeanServiceChain {
	BeanBuilder service(Class<?> clazz, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception;
}
