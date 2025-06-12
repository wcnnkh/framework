package run.soeasy.framework.io.load;

import run.soeasy.framework.core.spi.ConfigurableServices;
import run.soeasy.framework.io.Resource;

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
