package io.basc.framework.beans.repository;

import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.orm.repository.CurdRepository;
import io.basc.framework.util.StringUtils;

public class RepositoryBeanFactoryPostProcessor implements
		BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory)
			throws BeansException {
		for (Class<?> clazz : beanFactory.getContextClasses()) {
			if (!CurdRepository.class.isAssignableFrom(clazz)) {
				continue;
			}

			if (beanFactory.containsDefinition(clazz.getName())) {
				continue;
			}

			Repository repository = clazz.getAnnotation(Repository.class);
			if (repository == null) {
				continue;
			}

			String repositoryName = repository.name();
			if (StringUtils.isEmpty(repositoryName)) {
				repositoryName = repository.value().getName();
			}

			RepositoryBeanDefinition definition = new RepositoryBeanDefinition(
					beanFactory, clazz, repositoryName);
			beanFactory.registerDefinition(definition);
		}
	}
}
