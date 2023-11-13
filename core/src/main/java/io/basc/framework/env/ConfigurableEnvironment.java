package io.basc.framework.env;

import java.util.Properties;

import io.basc.framework.beans.factory.config.ConfigurableBeanFactory;
import io.basc.framework.convert.config.support.ConfigurableConversionService;
import io.basc.framework.convert.resolve.ResourceResolvers;
import io.basc.framework.env.properties.ConfigurableEnvironmentProperties;
import io.basc.framework.env.resource.ConfigurableEnvironmentResourceLoader;
import io.basc.framework.event.observe.Observable;
import io.basc.framework.io.Resource;
import io.basc.framework.io.resolver.PropertiesResolvers;
import io.basc.framework.util.registry.Registration;
import io.basc.framework.util.spi.Services;

public interface ConfigurableEnvironment extends Environment, ConfigurableBeanFactory {

	ConfigurableConversionService getConversionService();

	@Override
	ConfigurableEnvironmentProperties getProperties();

	PropertiesResolvers getPropertiesResolver();

	@Override
	ConfigurableEnvironmentResourceLoader getResourceLoader();

	ResourceResolvers getResourceResolver();

	@Override
	Services<Resource> getResources();

	default void setWorkPath(String path) {
		getProperties().put(WORK_PATH_PROPERTY, path);
	}

	Registration source(Observable<Properties> observable);

	Registration source(Resource resource);

	default Registration source(String location) {
		String locationUse = getProperties().replacePlaceholders(location);
		Resource resource = getResourceLoader().getResource(locationUse);
		return source(resource);
	}
}
