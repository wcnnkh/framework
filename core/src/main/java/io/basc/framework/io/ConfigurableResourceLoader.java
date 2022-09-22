package io.basc.framework.io;

import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ConfigurableServices;

public interface ConfigurableResourceLoader extends ResourceLoader, Configurable {

	ConfigurableServices<ProtocolResolver> getProtocolResolvers();

	ConfigurableServices<ResourceLoader> getResourceLoaders();
}
