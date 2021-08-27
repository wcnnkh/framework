package io.basc.framework.hikari.beans;

import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.db.DataBase;
import io.basc.framework.sql.ConnectionFactory;

import javax.sql.DataSource;

@Provider
public class HikariBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		HikariConfigDefinition hikariConfigDefinition = new HikariConfigDefinition(beanFactory);
		if (!beanFactory.containsDefinition(hikariConfigDefinition.getId())) {
			beanFactory.registerDefinition(hikariConfigDefinition);
		}

		HikariDataSourceDefinition hikariDataSourceDefinition = new HikariDataSourceDefinition(beanFactory);
		if (!beanFactory.containsDefinition(hikariConfigDefinition.getId())) {
			beanFactory.registerDefinition(hikariDataSourceDefinition);

			if (!beanFactory.isAlias(DataSource.class.getName())) {
				beanFactory.registerAlias(hikariDataSourceDefinition.getId(), DataSource.class.getName());
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
