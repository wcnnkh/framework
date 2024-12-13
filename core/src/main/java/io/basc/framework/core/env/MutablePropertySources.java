package io.basc.framework.core.env;

import io.basc.framework.util.StringUtils;
import io.basc.framework.util.spi.ConfigurableServices;

public class MutablePropertySources extends ConfigurableServices<PropertySource> {
	public PropertySource get(String name) {
		return filter((e) -> StringUtils.equals(e.getName(), name)).findFirst().orElse(null);
	}
}
