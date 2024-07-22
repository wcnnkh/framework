package io.basc.framework.env;

import io.basc.framework.register.Registration;
import io.basc.framework.text.placeholder.ConfigurablePlaceholderReplacer;
import io.basc.framework.transform.factory.PropertyFactory;
import io.basc.framework.transform.factory.config.EditablePropertyFactory;

public interface ConfigurablePropertyResolver extends PropertyResolver, EditablePropertyFactory {
	@Override
	ConfigurablePlaceholderReplacer getPlaceholderReplacer();

	Registration register(PropertyFactory propertyFactory);
}
