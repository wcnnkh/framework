package io.basc.framework.env;

import java.util.Properties;

import io.basc.framework.text.placeholder.ConfigurablePlaceholderReplacer;
import io.basc.framework.util.Registration;
import io.basc.framework.value.observe.Observable;

public interface ConfigurablePropertyResolver extends PropertyResolver {
	@Override
	ConfigurablePlaceholderReplacer getPlaceholderReplacer();

	void put(String key, Object value);

	Registration registerProperties(Observable<Properties> properties);
}
