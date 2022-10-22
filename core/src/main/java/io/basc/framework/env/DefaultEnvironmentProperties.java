package io.basc.framework.env;

import io.basc.framework.lang.Nullable;
import io.basc.framework.value.PropertyWrapper;
import io.basc.framework.value.support.DefaultPropertyFactory;

public class DefaultEnvironmentProperties extends DefaultPropertyFactory implements ConfigurableEnvironmentProperties {

	public DefaultEnvironmentProperties(@Nullable PropertyWrapper propertyWrapper) {
		super(propertyWrapper);
	}
}
