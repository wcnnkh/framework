package io.basc.framework.swagger.beans;

import java.util.Set;
import java.util.stream.Collectors;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.swagger.OpenAPIExtensionsReload;
import io.basc.framework.swagger.WebOpenApiContextBuilder;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;
import io.swagger.v3.oas.integration.OpenApiConfigurationException;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.integration.api.OpenApiContext;
import io.swagger.v3.oas.integration.api.OpenApiContextBuilder;

public class OpenApiContextDefinition extends DefaultBeanDefinition {

	public OpenApiContextDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, OpenApiContext.class);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(SwaggerConfiguration.class);
	}

	@Override
	public Object create() throws InstanceException {
		//重新加载扩展
		OpenAPIExtensionsReload.reload(beanFactory);
		
		SwaggerConfiguration configuration = beanFactory.getInstance(SwaggerConfiguration.class);
		if (CollectionUtils.isEmpty(configuration.getResourceClasses())
				&& StringUtils.isEmpty(configuration.getScannerClass())
				&& StringUtils.isEmpty(configuration.getReaderClass())) {
			Set<String> classNames = beanFactory.getContextClasses().stream().map((c) -> c.getName())
					.collect(Collectors.toSet());
			configuration.setResourceClasses(classNames);
		}
		OpenApiContextBuilder builder = new WebOpenApiContextBuilder().openApiConfiguration(configuration);
		try {
			return builder.buildContext(true);
		} catch (OpenApiConfigurationException e) {
			throw new InstanceException(e);
		}
	}
}
