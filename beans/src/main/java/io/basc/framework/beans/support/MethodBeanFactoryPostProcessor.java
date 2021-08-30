package io.basc.framework.beans.support;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.annotation.Bean;

import java.lang.reflect.Method;

public class MethodBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory)
			throws BeansException {
		for (Class<?> clz : beanFactory.getContextClasses()) {
			for (Method method : clz.getDeclaredMethods()) {
				Bean bean = method.getAnnotation(Bean.class);
				if (bean == null) {
					continue;
				}

				BeanDefinition beanDefinition = new MethodBeanDefinition(
						beanFactory, clz, method);
				beanFactory.registerDefinition(beanDefinition);
			}
		}
	}
}
