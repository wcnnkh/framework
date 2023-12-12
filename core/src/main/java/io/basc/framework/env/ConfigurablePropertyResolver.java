package io.basc.framework.env;

import java.util.Properties;

import io.basc.framework.observe.value.ObservableValue;
import io.basc.framework.text.placeholder.ConfigurablePlaceholderReplacer;
import io.basc.framework.util.Registration;
import io.basc.framework.value.EditablePropertyFactory;

public interface ConfigurablePropertyResolver extends PropertyResolver, EditablePropertyFactory {
	@Override
	ConfigurablePlaceholderReplacer getPlaceholderReplacer();

	Registration registerProperties(ObservableValue<? extends Properties> properties);
}
