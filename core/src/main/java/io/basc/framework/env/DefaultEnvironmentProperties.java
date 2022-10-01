package io.basc.framework.env;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.NamedEventDispatcher;
import io.basc.framework.event.support.SimpleStringNamedEventDispatcher;
import io.basc.framework.lang.Nullable;
import io.basc.framework.value.support.DefaultPropertyFactory;

public class DefaultEnvironmentProperties extends DefaultPropertyFactory implements ConfigurableEnvironmentProperties {
	public DefaultEnvironmentProperties(@Nullable PropertyWrapper propertyWrapper) {
		super(null, new SimpleStringNamedEventDispatcher<>(), propertyWrapper);
	}

	public DefaultEnvironmentProperties(@Nullable NamedEventDispatcher<String, ChangeEvent<String>> eventDispatcher,
			@Nullable PropertyWrapper propertyWrapper) {
		super(null, eventDispatcher, propertyWrapper);
	}
}
