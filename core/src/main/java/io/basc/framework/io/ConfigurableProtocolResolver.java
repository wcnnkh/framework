package io.basc.framework.io;

import io.basc.framework.util.spi.ConfigurableServices;

public class ConfigurableProtocolResolver extends ConfigurableServices<ProtocolResolver> implements ProtocolResolver {

	public ConfigurableProtocolResolver() {
		setServiceClass(ProtocolResolver.class);
	}

	@Override
	public Resource resolve(String location, ResourceLoader resourceLoader) {
		for (ProtocolResolver resolver : this) {
			Resource resource = resolver.resolve(location, resourceLoader);
			if (resource != null) {
				return resource;
			}
		}
		return null;
	}
}
