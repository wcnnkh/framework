package io.basc.framework.core.env;

import io.basc.framework.text.placeholder.ConfigurablePlaceholderReplacer;
import io.basc.framework.transform.factory.PropertyFactory;
import io.basc.framework.transform.factory.config.EditablePropertyFactory;
import io.basc.framework.util.Registration;

public interface ConfigurablePropertyResolver extends PropertyResolver, EditablePropertyFactory {
	@Override
	ConfigurablePlaceholderReplacer getPlaceholderReplacer();
	
	Registration register(PropertySource propertySource);
}
