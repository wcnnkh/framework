package io.basc.framework.core.env.config;

import io.basc.framework.core.env.PropertySource;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.spi.ConfigurableServices;

public class MutablePropertySources extends ConfigurableServices<PropertySource> {

	public MutablePropertySources() {
		setServiceClass(PropertySource.class);
	}

	public PropertySource get(String name) {
		return filter((e) -> StringUtils.equals(e.getName(), name)).findFirst().orElse(null);
	}
}
