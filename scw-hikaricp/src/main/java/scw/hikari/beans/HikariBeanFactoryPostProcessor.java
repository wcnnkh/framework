package scw.hikari.beans;

import javax.sql.DataSource;

import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.context.annotation.Provider;
import scw.db.database.DataBase;
import scw.sql.ConnectionFactory;

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
