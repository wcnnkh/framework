package io.basc.framework.env;

import io.basc.framework.value.PropertyFactories;

public interface ConfigurableEnvironmentProperties extends EnvironmentProperties {
	PropertyFactories getPropertyFactories();
}
