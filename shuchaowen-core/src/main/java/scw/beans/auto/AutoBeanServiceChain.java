package scw.beans.auto;

import scw.beans.BeanFactory;
import scw.beans.builder.BeanBuilder;
import scw.util.value.property.PropertyFactory;

public interface AutoBeanServiceChain {
	BeanBuilder service(Class<?> clazz, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception;
}
