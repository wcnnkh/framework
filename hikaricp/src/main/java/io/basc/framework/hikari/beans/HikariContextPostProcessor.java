package io.basc.framework.hikari.beans;

import javax.sql.DataSource;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.context.config.ConfigurableContext;
import io.basc.framework.context.config.ContextPostProcessor;
import io.basc.framework.db.DataBase;
import io.basc.framework.sql.ConnectionFactory;

@Provider
public class HikariContextPostProcessor implements ContextPostProcessor {

	@Override
	public void postProcessContext(ConfigurableContext context) throws Throwable {
		HikariConfigDefinition hikariConfigDefinition = new HikariConfigDefinition(context);
		if (!context.containsDefinition(hikariConfigDefinition.getId())) {
			context.registerDefinition(hikariConfigDefinition);
		}

		HikariDataSourceDefinition hikariDataSourceDefinition = new HikariDataSourceDefinition(context);
		if (!context.containsDefinition(hikariConfigDefinition.getId())) {
			context.registerDefinition(hikariDataSourceDefinition);

			if (!context.isAlias(DataSource.class.getName())) {
				context.registerAlias(hikariDataSourceDefinition.getId(), DataSource.class.getName());
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
