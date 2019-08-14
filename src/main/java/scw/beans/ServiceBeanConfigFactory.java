package scw.beans;

import scw.beans.annotation.Service;
import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;
import scw.core.utils.ResourceUtils;

/**
 * 此类只要类是存在的不可能出现获取不到的情况
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
				AnnotationBeanDefinition bean = new AnnotationBeanDefinition(valueWiredManager, beanFactory, propertyFactory, clz, filterNames);
				addBean(bean);
			}
		}
	}

}
