package io.basc.framework.io.resolver;

import java.nio.charset.Charset;
import java.util.Properties;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.io.Resource;
import io.basc.framework.io.WritableResource;
import io.basc.framework.lang.UnsupportedException;

public class PropertiesResolvers extends ConfigurableServices<PropertiesResolver> implements PropertiesResolver {

	public PropertiesResolvers() {
		super(PropertiesResolver.class);
	}

	public boolean canResolveProperties(Resource resource) {
		for (PropertiesResolver resolver : getServices()) {
			if (resolver.canResolveProperties(resource)) {
				return true;
			}
		}
		return false;
	}

	public void resolveProperties(Properties properties, Resource resource, Charset charset) {
		if (resource == null || !resource.exists()) {
			return;
		}

		for (PropertiesResolver resolver : getServices()) {
			if (resolver.canResolveProperties(resource)) {
				resolver.resolveProperties(properties, resource, charset);
				return;
			}
		}
		throw new UnsupportedException(resource.getDescription());
	}

	@Override
	public void persistenceProperties(Properties properties, WritableResource resource, Charset charset) {
		if (resource == null || !resource.exists()) {
			return;
		}

		for (PropertiesResolver resolver : getServices()) {
			if (resolver.canResolveProperties(resource)) {
				resolver.persistenceProperties(properties, resource, charset);
				return;
			}
		}
		throw new UnsupportedException(resource.getDescription());
	}
}
