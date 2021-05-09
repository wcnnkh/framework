package scw.env;

import java.util.Properties;

import scw.aop.ConfigurableProxyFactory;
import scw.convert.ConfigurableConversionService;
import scw.convert.resolve.ConfigurableResourceResolver;
import scw.event.Observable;
import scw.io.ConfigurableResourceLoader;
import scw.io.Resource;
import scw.io.event.ObservableResource;
import scw.io.resolver.ConfigurablePropertiesResolver;
import scw.net.message.convert.MessageConverters;
import scw.util.placeholder.ConfigurablePlaceholderReplacer;
import scw.value.ConfigurablePropertyFactory;
import scw.value.ListenablePropertyFactory;
import scw.value.PropertyFactory;

public interface ConfigurableEnvironment extends Environment,
		ConfigurablePropertyFactory, ListenablePropertyFactory,
		ConfigurableResourceLoader, ConfigurablePlaceholderReplacer {

	default void setWorkPath(String path) {
		put(WORK_PATH_PROPERTY, path);
	}
	
	default void loadProperties(Resource resource){
		Observable<Properties> observable = new ObservableResource<Properties>(resource, getPropertiesResolver().toPropertiesConverter());
		loadProperties(observable);
	}
	
	default void loadProperties(String resource) {
		loadProperties((String) resource, (String) null);
	}

	default void loadProperties(String resource, String charsetName) {
		loadProperties(null, resource, charsetName);
	}

	default void loadProperties(String keyPrefix, String location,
			String charsetName) {
		Observable<Properties> observable = getProperties(location, charsetName);
		loadProperties(keyPrefix, observable);
	}

	default void loadProperties(Observable<Properties> properties) {
		loadProperties(null, properties);
	}

	void loadProperties(String prefix, Observable<Properties> properties);

	ConfigurablePropertiesResolver getPropertiesResolver();

	ConfigurableProxyFactory getProxyFactory();

	ConfigurableConversionService getConversionService();

	ConfigurableResourceResolver getResourceResolver();

	void addPropertyFactory(PropertyFactory propertyFactory);
	
	MessageConverters getMessageConverter();
}
