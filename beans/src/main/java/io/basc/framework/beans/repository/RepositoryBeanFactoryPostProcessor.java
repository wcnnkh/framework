package io.basc.framework.beans.repository;

import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.orm.repository.Curd;

public class RepositoryBeanFactoryPostProcessor implements
		BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory)
			throws BeansException {
		for (Class<?> clazz : beanFactory.getContextClasses()) {
			if (!Curd.class.isAssignableFrom(clazz)) {
				continue;
			}

			if (beanFactory.containsDefinition(clazz.getName())) {
				continue;
			}

			Repository repository = clazz.getAnnotation(Repository.class);
			if (repository == null) {
				continue;
			}

			RepositoryBeanDefinition definition = new RepositoryBeanDefinition(
					beanFactory, clazz);
			beanFactory.registerDefinition(definition);
		}
	}
}
