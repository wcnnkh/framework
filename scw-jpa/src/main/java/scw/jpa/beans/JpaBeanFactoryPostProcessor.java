package scw.jpa.beans;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.context.annotation.Provider;
import scw.jpa.beans.annotation.Repository;

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
