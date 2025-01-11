package io.basc.framework.util.spi;

import io.basc.framework.util.collections.ServiceLoader;
import lombok.NonNull;

@FunctionalInterface
public interface ServiceLoaderDiscovery {
	<S> ServiceLoader<S> getServiceLoader(@NonNull Class<S> requiredType);
}
