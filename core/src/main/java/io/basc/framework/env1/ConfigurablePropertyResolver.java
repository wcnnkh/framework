package io.basc.framework.env1;

import java.util.Properties;

import io.basc.framework.event.observe.Observable;
import io.basc.framework.text.placeholder.ConfigurablePlaceholderReplacer;
import io.basc.framework.util.registry.Registration;

public interface ConfigurablePropertyResolver extends PropertyResolver {
	@Override
	ConfigurablePlaceholderReplacer getPlaceholderReplacer();

	void put(String key, Object value);

	Registration source(Observable<Properties> properties);
}
