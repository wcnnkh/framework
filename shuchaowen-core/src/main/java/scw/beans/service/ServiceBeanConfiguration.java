package scw.beans.service;

import java.util.Arrays;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.annotation.Service;
import scw.beans.configuration.AbstractBeanConfiguration;
import scw.core.Constants;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.PropertyFactory;

/**
 * 扫描service注解
 * 
 * @author shuchaowen
 *
 */
public class ServiceBeanConfiguration extends AbstractBeanConfiguration {
	private static Logger logger = LoggerUtils
			.getLogger(ServiceBeanConfiguration.class);
	
	public void init(BeanFactory beanFactory, PropertyFactory propertyFactory)
			throws Exception {
		for (Class<?> clz : ClassUtils.getClassSet(Arrays.asList(
				BeanUtils.getScanAnnotationPackageName(),
				Constants.SYSTEM_PACKAGE_NAME))) {
			Service service = clz.getAnnotation(Service.class);
			if (service == null) {
				continue;
			}

			if (!ReflectionUtils.isPresent(clz)) {
				logger.debug("not support class:{}", clz);
				continue;
			}

			ServiceBeanDefinition bean = new ServiceBeanDefinition(beanFactory,
					propertyFactory, clz);
			beanDefinitions.add(bean);
		}
	}
}
