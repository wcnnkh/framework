package scw.beans;

import scw.beans.annotation.Service;
import scw.beans.property.ValueWiredManager;
import scw.core.utils.ClassUtils;
import scw.util.value.property.PropertyFactory;

/**
 * 扫描service注解
 * 
 * @author shuchaowen
 *
 */
public class ServiceBeanConfigFactory extends AbstractBeanConfiguration {
	public ServiceBeanConfigFactory(ValueWiredManager valueWiredManager, BeanFactory beanFactory, PropertyFactory propertyFactory, String packageNames) throws Exception {
		for (Class<?> clz : ClassUtils.getClassSet(packageNames)) {
			Service service = clz.getAnnotation(Service.class);
			if (service != null) {
				ServiceBeanDefinition bean = new ServiceBeanDefinition(valueWiredManager, beanFactory, propertyFactory, clz);
				addBean(bean);
			}
		}
	}

}
