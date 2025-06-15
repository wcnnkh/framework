package run.soeasy.framework.io.resolver;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import run.soeasy.framework.core.spi.ConfigurableServices;
import run.soeasy.framework.io.Resource;

public class ConfigurablePropertiesResolver extends ConfigurableServices<PropertiesResolver>
		implements PropertiesResolver {

	public ConfigurablePropertiesResolver() {
		setServiceClass(PropertiesResolver.class);
	}

	public boolean canResolveProperties(Resource resource) {
		for (PropertiesResolver resolver : this) {
			if (resolver.canResolveProperties(resource)) {
				return true;
			}
		}
		return false;
	}

	public void resolveProperties(Properties properties, Resource resource)
			throws IOException, InvalidPropertiesFormatException {
		if (resource == null || !resource.exists()) {
			return;
		}

		for (PropertiesResolver resolver : this) {
			if (resolver.canResolveProperties(resource)) {
				resolver.resolveProperties(properties, resource);
				return;
			}
		}
		throw new UnsupportedOperationException(resource.getDescription());
	}

	@Override
	public void persistenceProperties(Properties properties, Resource resource) throws IOException {
		if (resource == null || !resource.exists()) {
			return;
		}

		for (PropertiesResolver resolver : this) {
			if (resolver.canResolveProperties(resource)) {
				resolver.persistenceProperties(properties, resource);
				return;
			}
		}
		throw new UnsupportedOperationException(resource.getDescription());
	}
}
