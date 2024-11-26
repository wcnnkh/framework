package io.basc.framework.core.env;

import io.basc.framework.transform.factory.PropertyFactory;
import io.basc.framework.transform.factory.config.EditablePropertyFactory;
import io.basc.framework.util.Registration;
import io.basc.framework.util.placeholder.ConfigurablePlaceholderReplacer;

public interface ConfigurablePropertyResolver extends PropertyResolver, EditablePropertyFactory {
	@Override
	ConfigurablePlaceholderReplacer getPlaceholderReplacer();
	
	Registration register(PropertySource propertySource);
}
