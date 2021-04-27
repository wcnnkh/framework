package scw.env;

import java.util.Map;
import java.util.Properties;

import scw.event.Observable;
import scw.util.placeholder.PropertyResolver;
import scw.value.ConfigurablePropertyFactory;
import scw.value.ListenablePropertyFactory;
import scw.value.PropertyFactory;
import scw.value.Value;

public interface PropertyManager extends PropertyResolver,
		ConfigurablePropertyFactory, ListenablePropertyFactory {
	void addPropertyFactory(PropertyFactory propertyFactory);

	Observable<Map<String, Value>> loadProperties(
			Observable<Properties> properties);

	Observable<Map<String, Value>> loadProperties(String prefix,
			Observable<Properties> properties);
}
