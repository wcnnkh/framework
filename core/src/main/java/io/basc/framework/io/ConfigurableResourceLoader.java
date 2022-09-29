package io.basc.framework.io;

import io.basc.framework.factory.ConfigurableServices;

public interface ConfigurableResourceLoader extends ResourceLoader {

	ConfigurableServices<ProtocolResolver> getProtocolResolvers();

	ConfigurableServices<ResourceLoader> getResourceLoaders();
}
