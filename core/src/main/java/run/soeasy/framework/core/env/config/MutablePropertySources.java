package run.soeasy.framework.core.env.config;

import run.soeasy.framework.core.env.PropertySource;
import run.soeasy.framework.util.StringUtils;
import run.soeasy.framework.util.spi.ConfigurableServices;

public class MutablePropertySources extends ConfigurableServices<PropertySource> {

	public MutablePropertySources() {
		setServiceClass(PropertySource.class);
	}

	public PropertySource get(String name) {
		return filter((e) -> StringUtils.equals(e.getName(), name)).findFirst().orElse(null);
	}
}
