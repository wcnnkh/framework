package scw.beans.method;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import scw.beans.BeanConfiguration;
import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.annotation.Bean;
import scw.core.Constants;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.PropertyFactory;

public class MethodBeanConfiguration implements BeanConfiguration {
	private static Logger logger = LoggerUtils
			.getConsoleLogger(MethodBeanConfiguration.class);

	public Collection<BeanDefinition> getBeans(BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		List<BeanDefinition> list = new LinkedList<BeanDefinition>();
		for (Class<?> clz : ClassUtils.getClassSet(Arrays.asList(
				BeanUtils.getScanAnnotationPackageName(),
				Constants.SYSTEM_PACKAGE_NAME))) {
			if (!ReflectionUtils.isPresent(clz)) {
				if (logger.isDebugEnabled()) {
					logger.debug("not support class:{}", clz);
				}
				continue;
			}

			for (Method method : clz.getDeclaredMethods()) {
				Bean bean = method.getAnnotation(Bean.class);
				if (bean == null) {
					continue;
				}

				BeanDefinition beanDefinition = new MethodBeanDefinition(
						beanFactory, propertyFactory, method.getReturnType(),
						clz, method);
				list.add(beanDefinition);
			}
		}
		return list;
	}
}
