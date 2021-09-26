package io.basc.framework.io.resolver;

import java.nio.charset.Charset;
import java.util.Properties;

import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.io.Resource;
import io.basc.framework.io.WritableResource;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.util.Assert;

public class PropertiesResolvers extends ConfigurableServices<PropertiesResolver> implements PropertiesResolver {
	private PropertiesResolver parentPropertiesResolver = DefaultPropertiesResolver.INSTANCE;

	public PropertiesResolvers() {
		super(PropertiesResolver.class);
	}

	public PropertiesResolvers(PropertiesResolver parentPropertiesResolver) {
		this();
		this.parentPropertiesResolver = parentPropertiesResolver;
	}

	public final PropertiesResolver getParentPropertiesResolver() {
		return parentPropertiesResolver;
	}

	public void setParentPropertiesResolver(PropertiesResolver parentPropertiesResolver) {
		Assert.requiredArgument(parentPropertiesResolver != null, "parentPropertiesResolver");
		this.parentPropertiesResolver = parentPropertiesResolver;
	}

	public boolean canResolveProperties(Resource resource) {
		for (PropertiesResolver resolver : this) {
			if (resolver.canResolveProperties(resource)) {
				return true;
			}
		}

		return parentPropertiesResolver.canResolveProperties(resource);
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

		if (parentPropertiesResolver.canResolveProperties(resource)) {
			parentPropertiesResolver.resolveProperties(properties, resource, charset);
			return;
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

		if (parentPropertiesResolver.canResolveProperties(resource)) {
			parentPropertiesResolver.persistenceProperties(properties, resource, charset);
			return;
		}
		throw new NotSupportedException(resource.getDescription());
	}
}
