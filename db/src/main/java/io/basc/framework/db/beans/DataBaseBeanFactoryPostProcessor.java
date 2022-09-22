package io.basc.framework.db.beans;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.db.Configurable;
import io.basc.framework.db.DB;
import io.basc.framework.env.ConfigurableEnvironment;
import io.basc.framework.env.EnvironmentPostProcessor;
import io.basc.framework.orm.repository.Repository;

@Provider
public class DataBaseBeanFactoryPostProcessor implements EnvironmentPostProcessor {

	@Override
	public void postProcessEnvironment(ConfigurableEnvironment environment) {
		if (!environment.containsDefinition(DB.class.getName())) {
			environment.registerDefinition(new DataBaseDefinition(environment));
		}

		if (!environment.containsDefinition(Configurable.class.getName())) {
			environment.registerDefinition(new ConfigurableDefinition(environment));
		}

		if (!environment.containsDefinition(Repository.class.getName())
				&& !environment.isAlias(Repository.class.getName())) {
			environment.registerAlias(Repository.class.getName(), DB.class.getName());
		}
	}

}
