package io.basc.framework.io;

import io.basc.framework.beans.factory.config.ConfigurableServices;

public class ConfigurableProtocolResolver extends ConfigurableServices<ProtocolResolver> implements ProtocolResolver {

	public ConfigurableProtocolResolver() {
		super(ProtocolResolver.class);
	}

	@Override
	public Resource resolve(String location, ResourceLoader resourceLoader) {
		for (ProtocolResolver resolver : getServices()) {
			Resource resource = resolver.resolve(location, resourceLoader);
			if (resource != null) {
				return resource;
			}
		}
		return null;
	}
}
