package io.basc.framework.io.resolver.support;

import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.io.Resource;
import io.basc.framework.io.resolver.ConfigurablePropertiesResolver;
import io.basc.framework.io.resolver.PropertiesResolver;
import io.basc.framework.lang.NotSupportedException;

import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class PropertiesResolvers extends DefaultPropertiesResolver
		implements ConfigurablePropertiesResolver, Comparator<PropertiesResolver>, Configurable {
	private ConfigurableServices<PropertiesResolver> services = new ConfigurableServices<>(PropertiesResolver.class);

	protected volatile List<PropertiesResolver> resolvers;

	public int compare(PropertiesResolver o1, PropertiesResolver o2) {
		return -1;
	}

	public void addPropertiesResolver(PropertiesResolver propertiesResolver) {
		services.addService(propertiesResolver);
	}

	@Override
	public Iterator<PropertiesResolver> iterator() {
		return services.iterator();
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		this.services.configure(serviceLoaderFactory);
	}

	public boolean canResolveProperties(Resource resource) {
		for (PropertiesResolver resolver : this) {
			if (resolver.canResolveProperties(resource)) {
				return true;
			}
		}
		return super.canResolveProperties(resource);
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

		if (super.canResolveProperties(resource)) {
			super.resolveProperties(properties, resource, charset);
			return;
		}
		throw new NotSupportedException(resource.getDescription());
	}
}
