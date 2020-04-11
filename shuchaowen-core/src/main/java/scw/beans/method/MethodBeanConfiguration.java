package scw.beans.method;

import java.lang.reflect.Method;
import java.util.Arrays;

import scw.beans.AbstractBeanConfiguration;
import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Bean;
import scw.beans.property.ValueWiredManager;
import scw.core.Constants;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ClassUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.value.property.PropertyFactory;

public class MethodBeanConfiguration extends AbstractBeanConfiguration {
	private static Logger logger = LoggerUtils.getLogger(MethodBeanConfiguration.class);
	
	public MethodBeanConfiguration(ValueWiredManager valueWiredManager, BeanFactory beanFactory,
			PropertyFactory propertyFactory, String packageNames) throws Exception {
		for (Class<?> clz : ClassUtils.getClassSet(Arrays.asList(packageNames, Constants.SYSTEM_PACKAGE_NAME))) {
			if(!ReflectionUtils.isPresent(clz)){
				if(logger.isDebugEnabled()){
					logger.debug("not support class:{}", clz);
				}
				continue;
			}
			
			for (Method method : clz.getDeclaredMethods()) {
				Bean bean = method.getAnnotation(Bean.class);
				if (bean == null) {
					continue;
				}

				BeanDefinition beanDefinition = new MethodBeanDefinition(valueWiredManager, beanFactory,
						propertyFactory, method.getReturnType(), clz, method);
				addBean(beanDefinition);
			}
		}
	}
}
