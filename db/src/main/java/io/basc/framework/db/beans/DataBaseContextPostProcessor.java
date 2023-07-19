package io.basc.framework.db.beans;

import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.context.config.ConfigurableContext;
import io.basc.framework.context.config.ContextPostProcessor;
import io.basc.framework.db.Configurable;
import io.basc.framework.db.DB;
import io.basc.framework.orm.repository.Repository;

@ConditionalOnParameters
public class DataBaseContextPostProcessor implements ContextPostProcessor {

	@Override
	public void postProcessContext(ConfigurableContext context) throws Throwable {
		if (!context.containsDefinition(DB.class.getName())) {
			context.registerDefinition(new DataBaseDefinition(context));
		}

		if (!context.containsDefinition(Configurable.class.getName())) {
			context.registerDefinition(new ConfigurableDefinition(context));
		}

		if (!context.containsDefinition(Repository.class.getName()) && !context.isAlias(Repository.class.getName())) {
			context.registerAlias(Repository.class.getName(), DB.class.getName());
		}
	}

}
