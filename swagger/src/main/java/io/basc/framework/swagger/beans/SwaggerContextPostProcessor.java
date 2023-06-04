package io.basc.framework.swagger.beans;

import io.basc.framework.beans.factory.BeanLifecycleEvent.Step;
import io.basc.framework.context.ConfigurableContext;
import io.basc.framework.context.ContextPostProcessor;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.env.Environment;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.web.HttpServiceRegistry;
import io.basc.framework.web.resource.StaticResourceRegistry;
import io.swagger.v3.oas.integration.api.OpenApiContext;

@Provider
public class SwaggerContextPostProcessor implements ContextPostProcessor {
	private static Logger logger = LoggerFactory.getLogger(SwaggerContextPostProcessor.class);

	@Override
	public void postProcessContext(ConfigurableContext context) {
		context.registerListener((info) -> {
			if (info.getBean() != null && info.getStep() == Step.AFTER_DEPENDENCE) {
				if (isEnableSwagger(context)) {
					if (info.getBean() instanceof HttpServiceRegistry) {
						logger.info("Enable swagger!");
						HttpServiceRegistry registry = (HttpServiceRegistry) info.getBean();
						registry.add("/swagger-ui/swagger.json", context.getInstance(SwaggerUiHttpService.class));

						StaticResourceRegistry staticResourceRegistry = new StaticResourceRegistry();
						staticResourceRegistry.add("/swagger-ui/*", "classpath:/io/basc/framework/swagger");
						registry.add(staticResourceRegistry);
					}
				}
			}
		});

		if (!context.containsDefinition(OpenApiContext.class.getName())) {
			OpenApiContextDefinition definition = new OpenApiContextDefinition(context);
			context.registerDefinition(definition);
		}
	}

	private boolean isEnableSwagger(Environment environment) {
		return environment.getProperties().getAsBoolean("swagger.enable");
	}
}
