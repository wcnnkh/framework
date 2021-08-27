package io.basc.framework.jpa.beans;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.jpa.beans.annotation.Repository;

@Provider
public class JpaBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory)
			throws BeansException {
		for (Class<?> clazz : beanFactory.getContextClasses()) {
			if (clazz.isAnnotationPresent(Repository.class)) {
				BeanDefinition definition = new RepositoryDefinition(
						beanFactory, clazz);
				if (!beanFactory.containsDefinition(definition.getId())) {
					beanFactory.registerDefinition(definition);
				}
			}
		}
	}
}
