package scw.io.resolver.support;

import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import scw.instance.Configurable;
import scw.instance.ConfigurableServices;
import scw.instance.ServiceLoaderFactory;
import scw.io.Resource;
import scw.io.resolver.ConfigurablePropertiesResolver;
import scw.io.resolver.PropertiesResolver;
import scw.lang.NotSupportedException;

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
