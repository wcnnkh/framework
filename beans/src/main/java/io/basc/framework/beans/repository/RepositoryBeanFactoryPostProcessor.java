package io.basc.framework.beans.repository;

import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.BeanlifeCycleEvent.Step;
import io.basc.framework.orm.repository.CurdRepository;
import io.basc.framework.orm.repository.CurdRepositoryRegistry;
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

		beanFactory
				.getLifecycleDispatcher()
				.registerListener(
						(e) -> {
							if (e.getSource() instanceof CurdRepositoryRegistry) {
								if (e.getStep() == Step.AFTER_INIT) {
									CurdRepositoryRegistry curdRepositoryRegistry = (CurdRepositoryRegistry) e
											.getSource();
									if (curdRepositoryRegistry.getRepository() == null) {
										if (e.getBeanFactory()
												.isInstance(
														io.basc.framework.orm.repository.Repository.class)) {
											curdRepositoryRegistry
													.setRepository(e
															.getBeanFactory()
															.getInstance(
																	io.basc.framework.orm.repository.Repository.class));
										}
									}
								}
							}
						});
	}
}
