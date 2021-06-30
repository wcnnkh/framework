package scw.db.beans;

import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.context.annotation.Provider;
import scw.db.ConnectionConfig;
import scw.db.ConnectionPoolConfig;
import scw.db.DatabaseConfig;

@Provider
public class ConfigBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		if (!beanFactory.isAlias(ConnectionPoolConfig.class.getName())) {
			beanFactory.registerAlias(DatabaseConfig.class.getName(), ConnectionPoolConfig.class.getName());
		}

		if (!beanFactory.isAlias(ConnectionConfig.class.getName())) {
			beanFactory.registerAlias(ConnectionPoolConfig.class.getName(), ConnectionConfig.class.getName());
		}
	}

}
