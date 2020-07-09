package scw.beans.service;

import scw.beans.AbstractBeanConfiguration;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.annotation.Service;
import scw.core.Constants;
import scw.core.reflect.ReflectionUtils;
import scw.util.PackageScan;
import scw.value.property.PropertyFactory;

/**
 * 扫描service注解
 * 
 * @author shuchaowen
 *
 */
public class ServiceBeanConfiguration extends AbstractBeanConfiguration {

	public void init(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception {
		for (Class<?> clz : PackageScan.getInstance().getClasses(BeanUtils.getScanAnnotationPackageName(),
				Constants.SYSTEM_PACKAGE_NAME)) {
			if (!ReflectionUtils.isPresent(clz)) {
				continue;
			}

			Service service = clz.getAnnotation(Service.class);
			if (service == null) {
				continue;
			}

			ServiceBeanDefinition bean = new ServiceBeanDefinition(beanFactory, propertyFactory, clz);
			beanDefinitions.add(bean);
		}
	}
}
