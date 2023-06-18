package io.basc.framework.druid.beans;

import javax.sql.DataSource;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.context.config.ConfigurableContext;
import io.basc.framework.context.config.ContextPostProcessor;
import io.basc.framework.db.DataBase;
import io.basc.framework.sql.ConnectionFactory;

@Provider
public class DruidContextostProcessor implements ContextPostProcessor {

	@Override
	public void postProcessContext(ConfigurableContext context) throws Throwable {
		BeanDefinition definition = new DruidDataSourceDefinition(context);
		if (!context.containsDefinition(definition.getId())) {
			context.registerDefinition(definition);

			if (!context.isAlias(DataSource.class.getName())) {
				context.registerAlias(definition.getId(), DataSource.class.getName());
			}
		}

		ConnectionFactoryDefinition connectionFactoryDefinition = new ConnectionFactoryDefinition(context);
		if (!context.containsDefinition(connectionFactoryDefinition.getId())) {
			context.registerDefinition(connectionFactoryDefinition);
			if (!context.isAlias(ConnectionFactory.class.getName())) {
				context.registerAlias(connectionFactoryDefinition.getId(), ConnectionFactory.class.getName());
			}
		}

		if (!context.containsDefinition(DataBase.class.getName())) {
			context.registerDefinition(new DataBaseDefinition(context));
		}
	}

}
