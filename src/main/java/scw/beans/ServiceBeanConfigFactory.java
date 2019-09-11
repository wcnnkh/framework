package scw.beans;

import scw.beans.annotation.Service;
import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;
import scw.core.utils.ResourceUtils;

/**
 * 扫描service注解
 * 
 * @author shuchaowen
 *
 */
public class ServiceBeanConfigFactory extends AbstractBeanConfigFactory {
	public ServiceBeanConfigFactory(ValueWiredManager valueWiredManager, BeanFactory beanFactory, PropertyFactory propertyFactory, String packageNames,
			String[] filterNames) throws Exception {
		for (Class<?> clz : ResourceUtils.getClassList(packageNames)) {
			Service service = clz.getAnnotation(Service.class);
			if (service != null) {
				CommonBeanDefinition bean = new CommonBeanDefinition(valueWiredManager, beanFactory, propertyFactory, clz, filterNames);
				addBean(bean);
			}
		}
	}

}
