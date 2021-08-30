package io.basc.framework.swagger.beans;

import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeanlifeCycleEvent.Step;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.env.Environment;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.web.HttpServiceRegistry;
import io.basc.framework.web.resource.StaticResourceRegistry;
import io.swagger.v3.oas.integration.api.OpenApiContext;

@Provider
public class SwaggerBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	private static Logger logger = LoggerFactory.getLogger(SwaggerBeanFactoryPostProcessor.class);

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		beanFactory.getLifecycleDispatcher().registerListener((info) -> {
			if (info.getSource() != null && info.getStep() == Step.AFTER_DEPENDENCE) {
				if (isEnableSwagger(beanFactory.getEnvironment())) {
					if (info.getSource() instanceof HttpServiceRegistry) {
						logger.info("Enable swagger!");
						HttpServiceRegistry registry = (HttpServiceRegistry) info.getSource();
						registry.add("/swagger-ui/swagger.json", beanFactory.getInstance(SwaggerUiHttpService.class));

						StaticResourceRegistry staticResourceRegistry = new StaticResourceRegistry();
						staticResourceRegistry.add("/swagger-ui/*", "classpath:/io/basc/framework/swagger");
						registry.add(staticResourceRegistry);
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
