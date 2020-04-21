package scw.beans.auto;

import scw.beans.BeanBuilder;
import scw.beans.BeanFactory;
import scw.util.value.property.PropertyFactory;

public interface AutoBeanService {
	BeanBuilder doService(Class<?> clazz, BeanFactory beanFactory,
			PropertyFactory propertyFactory, AutoBeanServiceChain serviceChain) throws Exception;
}
