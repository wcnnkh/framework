package scw.beans;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import scw.beans.annotation.Service;
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
public class ServiceBeanConfiguration implements BeanConfiguration {
	private static Logger logger = LoggerUtils
			.getLogger(ServiceBeanConfiguration.class);

	public Collection<BeanDefinition> getBeans(BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		List<BeanDefinition> beanDefinitions = new LinkedList<BeanDefinition>();
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
		return beanDefinitions;
	}
}
