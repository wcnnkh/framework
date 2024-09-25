package io.basc.framework.util.spi;

import io.basc.framework.util.ServiceLoader;

@FunctionalInterface
public interface ServiceLoaderDiscovery {
	<S> ServiceLoader<S> getServiceLoader(Class<S> requiredType);
}
