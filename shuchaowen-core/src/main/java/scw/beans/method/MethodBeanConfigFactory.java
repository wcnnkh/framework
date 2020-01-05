package scw.beans.method;

import java.lang.reflect.Method;

import scw.beans.AbstractBeanConfiguration;
import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Bean;
import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;
import scw.core.utils.ClassUtils;
import scw.util.StringParseValueFactory;

public class MethodBeanConfigFactory extends AbstractBeanConfiguration {
	public MethodBeanConfigFactory(ValueWiredManager valueWiredManager, BeanFactory beanFactory,
			PropertyFactory propertyFactory, String packageNames) throws Exception {
		for (Class<?> clz : ClassUtils.getClassList(packageNames)) {
			for (Method method : clz.getDeclaredMethods()) {
				Bean bean = method.getAnnotation(Bean.class);
				if (bean == null) {
					continue;
				}

				BeanDefinition beanDefinition = new MethodBeanDefinition(valueWiredManager, beanFactory,
						propertyFactory, method.getReturnType(), clz, method,
						StringParseValueFactory.STRING_PARSE_VALUE_FACTORY);
				addBean(beanDefinition);
			}
		}
	}
}
