package io.basc.framework.swagger.beans;

import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.BeanlifeCycleEvent.Step;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.env.Environment;
import io.basc.framework.web.HttpServiceRegistry;
import io.basc.framework.web.resource.StaticResourceRegistry;
import io.swagger.v3.oas.integration.api.OpenApiContext;

@Provider
public class SwaggerBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		beanFactory.getLifecycleDispatcher().registerListener((info) -> {
			if (info.getSource() != null && info.getStep() == Step.AFTER_DEPENDENCE) {
				if (isEnableSwagger(beanFactory.getEnvironment())) {
					if (info.getSource() instanceof StaticResourceRegistry) {
						StaticResourceRegistry staticResourceRegistry = (StaticResourceRegistry) info.getSource();
						staticResourceRegistry.add("/swagger-ui/*", "/io/basc/framework/swagger/");
					}

					if (info.getSource() instanceof HttpServiceRegistry) {
						HttpServiceRegistry registry = (HttpServiceRegistry) info.getSource();
						registry.add("/swagger-ui/swagger.json",
								beanFactory.getInstance(SwaggerUiHttpService.class));
					}
				}
			}
		});

		if (!beanFactory.containsDefinition(OpenApiContext.class.getName())) {
			OpenApiContextDefinition definition = new OpenApiContextDefinition(beanFactory);
			beanFactory.registerDefinition(definition);
		}
	}

	private boolean isEnableSwagger(Environment environment) {
		return environment.getValue("swagger.enable", boolean.class, false);
	}
}
