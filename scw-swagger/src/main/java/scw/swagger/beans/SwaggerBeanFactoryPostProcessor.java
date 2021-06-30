package scw.swagger.beans;

import io.swagger.v3.oas.integration.api.OpenApiContext;
import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeanlifeCycleEvent.Step;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.context.annotation.Provider;
import scw.env.Environment;
import scw.web.HttpServiceRegistry;
import scw.web.support.StaticResourceRegistry;

@Provider
public class SwaggerBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		beanFactory.getLifecycleDispatcher().registerListener((info) -> {
			if (info.getSource() != null && info.getStep() == Step.AFTER_DEPENDENCE) {
				if (isEnableSwagger(beanFactory.getEnvironment())) {
					if (info.getSource() instanceof StaticResourceRegistry) {
						StaticResourceRegistry staticResourceRegistry = (StaticResourceRegistry) info.getSource();
						staticResourceRegistry.register("/swagger-ui/*", "/");
					}

					if (info.getSource() instanceof HttpServiceRegistry) {
						HttpServiceRegistry registry = (HttpServiceRegistry) info.getSource();
						registry.register("/swagger-ui/swagger.json",
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
