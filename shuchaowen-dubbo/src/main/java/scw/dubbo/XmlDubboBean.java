package scw.dubbo;

import org.apache.dubbo.config.ReferenceConfig;

import scw.beans.BeanFactory;
import scw.beans.DefaultBeanDefinition;
import scw.util.value.property.PropertyFactory;

public final class XmlDubboBean extends DefaultBeanDefinition {

	public XmlDubboBean(BeanFactory beanFactory, PropertyFactory propertyFactory,
			Class<?> type, ReferenceConfig<?> referenceConfig) {
		super(beanFactory, propertyFactory, type, new DubboBeanBuilder(beanFactory, propertyFactory, type, referenceConfig));
	}
}
