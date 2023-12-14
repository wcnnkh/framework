package io.basc.framework.env;

import io.basc.framework.text.placeholder.ConfigurablePlaceholderReplacer;
import io.basc.framework.util.Registration;
import io.basc.framework.value.EditablePropertyFactory;
import io.basc.framework.value.PropertyFactory;

public interface ConfigurablePropertyResolver extends PropertyResolver, EditablePropertyFactory {
	@Override
	ConfigurablePlaceholderReplacer getPlaceholderReplacer();

	Registration register(PropertyFactory propertyFactory);
}
