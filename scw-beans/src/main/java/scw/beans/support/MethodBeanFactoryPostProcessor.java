package scw.beans.support;

import java.lang.reflect.Method;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.annotation.Bean;

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
