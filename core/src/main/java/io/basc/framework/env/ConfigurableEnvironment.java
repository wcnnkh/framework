package io.basc.framework.env;

import java.util.Properties;

import io.basc.framework.convert.lang.ConfigurableConversionService;
import io.basc.framework.convert.resolve.ResourceResolvers;
import io.basc.framework.event.Observable;
import io.basc.framework.factory.ConfigurableBeanFactory;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.io.resolver.PropertiesResolvers;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.placeholder.ConfigurablePlaceholderReplacer;

public interface ConfigurableEnvironment extends Environment, ConfigurableBeanFactory {

	@Override
	ConfigurableEnvironmentProperties getProperties();

	@Override
	ConfigurableEnvironmentResourceLoader getResourceLoader();

	default void setWorkPath(String path) {
		getProperties().put(WORK_PATH_PROPERTY, path);
	}

	default void loadProperties(Resource resource) {
		if (!getPropertiesResolver().canResolveProperties(resource)) {
			throw new UnsupportedException(resource.getDescription());
		}

		Observable<Properties> observable = resource.map(ResourceUtils.toPropertiesConverter(getPropertiesResolver()));
		loadProperties(observable);
	}

	default void loadProperties(String resource) {
		loadProperties((String) resource, (String) null);
	}

	default void loadProperties(String location, String charsetName) {
		Observable<Properties> observable = getProperties(location, charsetName);
		loadProperties(observable);
	}

	void loadProperties(Observable<Properties> properties);

	ConfigurablePlaceholderReplacer getPlaceholderReplacer();

	PropertiesResolvers getPropertiesResolver();

	ConfigurableConversionService getConversionService();

	ResourceResolvers getResourceResolver();
}
