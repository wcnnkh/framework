package io.basc.framework.io.resolver;

import java.nio.charset.Charset;
import java.util.Properties;

import io.basc.framework.io.Resource;
import io.basc.framework.io.WritableResource;
import io.basc.framework.lang.UnsupportedException;

public class ConfigurablePropertiesResolvers extends PropertiesResolvers {
	private PropertiesResolver parentPropertiesResolver = DefaultPropertiesResolver.getInstance();

	public PropertiesResolver getParentPropertiesResolver() {
		return parentPropertiesResolver;
	}

	public void setParentPropertiesResolver(PropertiesResolver parentPropertiesResolver) {
		this.parentPropertiesResolver = parentPropertiesResolver;
	}

	@Override
	public boolean canResolveProperties(Resource resource) {
		return super.canResolveProperties(resource)
				|| (parentPropertiesResolver != null && parentPropertiesResolver.canResolveProperties(resource));
	}

	@Override
	public void resolveProperties(Properties properties, Resource resource, Charset charset) {
		if (super.canResolveProperties(resource)) {
			super.resolveProperties(properties, resource, charset);
			return;
		}

		if (parentPropertiesResolver != null && parentPropertiesResolver.canResolveProperties(resource)) {
			parentPropertiesResolver.resolveProperties(properties, resource, charset);
			return;
		}
		throw new UnsupportedException(resource.getDescription());
	}

	@Override
	public void persistenceProperties(Properties properties, WritableResource resource, Charset charset) {
		if (super.canResolveProperties(resource)) {
			super.persistenceProperties(properties, resource, charset);
			return;
		}

		if (parentPropertiesResolver != null && parentPropertiesResolver.canResolveProperties(resource)) {
			parentPropertiesResolver.persistenceProperties(properties, resource, charset);
			return;
		}
		throw new UnsupportedException(resource.getDescription());
	}
}
