package io.basc.framework.io.resolver;

import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.io.Resource;
import io.basc.framework.io.WritableResource;
import io.basc.framework.lang.NotSupportedException;

import java.nio.charset.Charset;
import java.util.Properties;

public class PropertiesResolvers extends ConfigurableServices<PropertiesResolver> implements PropertiesResolver {

	public PropertiesResolvers() {
		super(PropertiesResolver.class);
		setAfterService(DefaultPropertiesResolver.INSTANCE);
	}

	public boolean canResolveProperties(Resource resource) {
		for (PropertiesResolver resolver : this) {
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

		for (PropertiesResolver resolver : this) {
			if (resolver.canResolveProperties(resource)) {
				resolver.resolveProperties(properties, resource, charset);
				return;
			}
		}
		throw new NotSupportedException(resource.getDescription());
	}

	@Override
	public void persistenceProperties(Properties properties, WritableResource resource, Charset charset) {
		if (resource == null || !resource.exists()) {
			return;
		}

		for (PropertiesResolver resolver : this) {
			if (resolver.canResolveProperties(resource)) {
				resolver.persistenceProperties(properties, resource, charset);
				return;
			}
		}
		throw new NotSupportedException(resource.getDescription());
	}
}
