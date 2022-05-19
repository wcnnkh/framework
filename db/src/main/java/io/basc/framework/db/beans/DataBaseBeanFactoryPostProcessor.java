package io.basc.framework.db.beans;

import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.db.Configurable;
import io.basc.framework.db.DB;
import io.basc.framework.orm.repository.Repository;

@Provider
public class DataBaseBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		if (!beanFactory.containsDefinition(DB.class.getName())) {
			beanFactory.registerDefinition(new DataBaseDefinition(beanFactory));
		}

		if (!beanFactory.containsDefinition(Configurable.class.getName())) {
			beanFactory.registerDefinition(new ConfigurableDefinition(beanFactory));
		}

		if (!beanFactory.containsDefinition(Repository.class.getName())
				&& !beanFactory.isAlias(Repository.class.getName())) {
			beanFactory.registerAlias(Repository.class.getName(), DB.class.getName());
		}
	}

}
