package scw.swagger.beans;

import io.swagger.v3.oas.integration.api.OpenApiContext;
import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeanlifeCycleEvent.Step;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.context.annotation.Provider;
import scw.web.support.StaticResourceRegistry;

@Provider
public class SwaggerBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		beanFactory.getLifecycleDispatcher().registerListener((info) -> {
			if (info.getSource() != null && info.getStep() == Step.AFTER_DEPENDENCE) {
				if (info.getSource() instanceof StaticResourceRegistry) {
					StaticResourceRegistry staticResourceRegistry = (StaticResourceRegistry) info.getSource();
					staticResourceRegistry.register("/swagger-ui/*", "/");
				}
			}
		});

		if (!beanFactory.containsDefinition(OpenApiContext.class.getName())) {
			OpenApiContextDefinition definition = new OpenApiContextDefinition(beanFactory);
			beanFactory.registerDefinition(definition);
		}
	}

}
