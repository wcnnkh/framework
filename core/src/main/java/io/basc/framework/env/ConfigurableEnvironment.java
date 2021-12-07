package io.basc.framework.env;

import io.basc.framework.convert.lang.ConversionServices;
import io.basc.framework.convert.resolve.ResourceResolvers;
import io.basc.framework.event.Observable;
import io.basc.framework.io.ConfigurableResourceLoader;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.io.event.ObservableResource;
import io.basc.framework.io.resolver.PropertiesResolvers;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.util.placeholder.ConfigurablePlaceholderReplacer;
import io.basc.framework.value.ConfigurablePropertyFactory;
import io.basc.framework.value.PropertyFactory;

import java.util.Properties;

public interface ConfigurableEnvironment extends Environment, ConfigurablePropertyFactory, ConfigurableResourceLoader {

	default void setWorkPath(String path) {
		put(WORK_PATH_PROPERTY, path);
	}

	default void loadProperties(Resource resource) {
		if (!getPropertiesResolver().canResolveProperties(resource)) {
			throw new NotSupportedException(resource.getDescription());
		}

		Observable<Properties> observable = new ObservableResource<Properties>(resource,
				ResourceUtils.toPropertiesConverter(getPropertiesResolver()));
		loadProperties(observable);
	}

	default void loadProperties(String resource) {
		loadProperties((String) resource, (String) null);
	}

	default void loadProperties(String resource, String charsetName) {
		loadProperties(null, resource, charsetName);
	}

	default void loadProperties(String keyPrefix, String location, String charsetName) {
		Observable<Properties> observable = getProperties(location, charsetName);
		loadProperties(keyPrefix, observable);
	}

	default void loadProperties(Observable<Properties> properties) {
		loadProperties(null, properties);
	}

	void loadProperties(String prefix, Observable<Properties> properties);

	ConfigurablePlaceholderReplacer getPlaceholderReplacer();

	PropertiesResolvers getPropertiesResolver();

	ConversionServices getConversionService();

	ResourceResolvers getResourceResolver();

	void addFactory(PropertyFactory propertyFactory);
}
