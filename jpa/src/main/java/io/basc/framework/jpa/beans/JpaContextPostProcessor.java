package io.basc.framework.jpa.beans;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.context.ConfigurableContext;
import io.basc.framework.context.ContextPostProcessor;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.jpa.beans.annotation.Repository;

@Provider
public class JpaContextPostProcessor implements ContextPostProcessor {
	
	@Override
	public void postProcessContext(ConfigurableContext context) {
		for (Class<?> clazz : context.getContextClasses()) {
			if (clazz.isAnnotationPresent(Repository.class)) {
				BeanDefinition definition = new RepositoryDefinition(context, clazz);
				if (!context.containsDefinition(definition.getId())) {
					context.registerDefinition(definition);
				}
			}
		}
	}

}
