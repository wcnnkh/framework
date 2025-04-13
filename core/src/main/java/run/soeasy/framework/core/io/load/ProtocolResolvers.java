package run.soeasy.framework.core.io.load;

import run.soeasy.framework.core.io.Resource;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class ProtocolResolvers extends ConfigurableServices<ProtocolResolver> implements ProtocolResolver {

	public ProtocolResolvers() {
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
