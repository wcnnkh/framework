package io.basc.framework.io;

import io.basc.framework.beans.factory.config.ConfigurableServices;

public class ProtocolResolvers extends ConfigurableServices<ProtocolResolver> implements ProtocolResolver {

	public ProtocolResolvers() {
		setServiceClass(ProtocolResolver.class);
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
