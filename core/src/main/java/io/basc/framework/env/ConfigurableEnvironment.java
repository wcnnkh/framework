package io.basc.framework.env;

import java.util.Properties;

import io.basc.framework.convert.lang.ConfigurableConversionService;
import io.basc.framework.convert.resolve.ResourceResolvers;
import io.basc.framework.event.Observable;
import io.basc.framework.factory.ConfigurableBeanFactory;
import io.basc.framework.factory.ConfigurableServiceLoader;
import io.basc.framework.io.Resource;
import io.basc.framework.io.resolver.PropertiesResolvers;
import io.basc.framework.util.Registration;
import io.basc.framework.util.placeholder.ConfigurablePlaceholderReplacer;

public interface ConfigurableEnvironment extends Environment, ConfigurableBeanFactory {

	ConfigurableConversionService getConversionService();

	ConfigurablePlaceholderReplacer getPlaceholderReplacer();

	@Override
	ConfigurableEnvironmentProperties getProperties();

	PropertiesResolvers getPropertiesResolver();

	@Override
	ConfigurableEnvironmentResourceLoader getResourceLoader();

	ResourceResolvers getResourceResolver();

	@Override
	ConfigurableServiceLoader<Resource> getResources();

	default void setWorkPath(String path) {
		getProperties().put(WORK_PATH_PROPERTY, path);
	}

	Registration source(Observable<Properties> observable);

	Registration source(Resource resource);

	default Registration source(String location) {
		return source(getResourceLoader().getResource(location));
	}
}
