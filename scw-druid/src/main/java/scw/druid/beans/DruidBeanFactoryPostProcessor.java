package scw.druid.beans;

import javax.sql.DataSource;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.context.annotation.Provider;
import scw.db.DataBase;
import scw.sql.ConnectionFactory;

@Provider
public class DruidBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		BeanDefinition definition = new DruidDataSourceDefinition(beanFactory);
		if (!beanFactory.containsDefinition(definition.getId())) {
			beanFactory.registerDefinition(definition);

			if (!beanFactory.isAlias(DataSource.class.getName())) {
				beanFactory.registerAlias(definition.getId(), DataSource.class.getName());
			}
		}

		ConnectionFactoryDefinition connectionFactoryDefinition = new ConnectionFactoryDefinition(beanFactory);
		if (!beanFactory.containsDefinition(connectionFactoryDefinition.getId())) {
			beanFactory.registerDefinition(connectionFactoryDefinition);
			if (!beanFactory.isAlias(ConnectionFactory.class.getName())) {
				beanFactory.registerAlias(connectionFactoryDefinition.getId(), ConnectionFactory.class.getName());
			}
		}

		if (!beanFactory.containsDefinition(DataBase.class.getName())) {
			beanFactory.registerDefinition(new DataBaseDefinition(beanFactory));
		}
	}
}
