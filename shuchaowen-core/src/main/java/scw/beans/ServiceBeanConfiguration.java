package scw.beans;

import scw.beans.annotation.Service;
import scw.beans.property.ValueWiredManager;
import scw.core.Constants;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.util.value.property.PropertyFactory;

/**
 * 扫描service注解
 * 
 * @author shuchaowen
 *
 */
public class ServiceBeanConfiguration extends AbstractBeanConfiguration {
	public ServiceBeanConfiguration(ValueWiredManager valueWiredManager, BeanFactory beanFactory, PropertyFactory propertyFactory, String packageNames) throws Exception {
		for (Class<?> clz : ClassUtils.getClassSet(packageNames, Constants.DEFAULT_ROOT_PACKAGE_PREFIX)) {
			Service service = clz.getAnnotation(Service.class);
			if (service == null) {
				continue;
			}
			
			if(!ReflectionUtils.isPresent(clz)){
				logger.debug("not support class:{}", clz);
				continue;
			}
			
			ServiceBeanDefinition bean = new ServiceBeanDefinition(valueWiredManager, beanFactory, propertyFactory, clz);
			addBean(bean);
		}
	}

}
