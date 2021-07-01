package scw.db.beans;

import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.context.annotation.Provider;
import scw.db.DB;

@Provider
public class DataBaseBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		if (!beanFactory.containsDefinition(DB.class.getName())) {
			beanFactory.registerDefinition(new DataBaseDefinition(beanFactory));
		}
	}

}
