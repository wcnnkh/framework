package io.basc.framework.env;

import io.basc.framework.util.Services;
import io.basc.framework.value.PropertyFactory;

public interface ConfigurableEnvironmentProperties extends EnvironmentProperties {
	Services<PropertyFactory> getTandemFactories();
}
