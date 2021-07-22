package scw.swagger.beans;

import java.util.Set;
import java.util.stream.Collectors;

import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.integration.api.OpenApiContextBuilder;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.instance.InstanceException;
import scw.swagger.WebOpenApiContextBuilder;

public class OpenApiContextDefinition extends DefaultBeanDefinition {

	public OpenApiContextDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, OpenApiContext.class);
	}

	@Override
	public boolean isInstance() {
		return true;
	}

	@Override
	public Object create() throws InstanceException {
		Set<String> classNames = beanFactory.getContextClasses().stream().map((c) -> c.getName())
				.collect(Collectors.toSet());
		SwaggerConfiguration configuration = new SwaggerConfiguration();
		configuration = configuration.resourceClasses(classNames);
		OpenApiContextBuilder builder = new WebOpenApiContextBuilder().openApiConfiguration(configuration);
		try {
			return builder.buildContext(true);
		} catch (OpenApiConfigurationException e) {
			throw new InstanceException(e);
		}
	}
}
