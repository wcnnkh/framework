package scw.env;

import java.util.Map;
import java.util.Properties;

import scw.event.Observable;
import scw.util.PropertyResolver;
import scw.value.Value;
import scw.value.factory.ConfigurablePropertyFactory;
import scw.value.factory.ConvertibleObservableValueFactory;
import scw.value.factory.ObservablePropertyFactory;
import scw.value.factory.PropertyFactory;

public interface PropertyManager extends PropertyResolver,
		ConfigurablePropertyFactory, ObservablePropertyFactory,
		ConvertibleObservableValueFactory<String> {
	void addPropertyFactory(PropertyFactory propertyFactory);

	Observable<Map<String, Value>> loadProperties(
			Observable<Properties> properties);

	Observable<Map<String, Value>> loadProperties(String prefix,
			Observable<Properties> properties);
}
