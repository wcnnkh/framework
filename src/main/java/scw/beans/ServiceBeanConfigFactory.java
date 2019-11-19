package scw.beans;

import scw.beans.annotation.Service;
import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;
import scw.core.utils.ClassUtils;

/**
 * 扫描service注解
 * 
 * @author shuchaowen
 *
 */
public class ServiceBeanConfigFactory extends AbstractBeanConfigFactory {
	public ServiceBeanConfigFactory(ValueWiredManager valueWiredManager, BeanFactory beanFactory, PropertyFactory propertyFactory, String packageNames) throws Exception {
		for (Class<?> clz : ClassUtils.getClassList(packageNames)) {
			Service service = clz.getAnnotation(Service.class);
			if (service != null) {
				ServiceBeanDefinition bean = new ServiceBeanDefinition(valueWiredManager, beanFactory, propertyFactory, clz);
				addBean(bean);
			}
		}
	}

}
