package io.basc.framework.util.spi;

import io.basc.framework.util.ServiceLoader;
import lombok.NonNull;

@FunctionalInterface
public interface ServiceLoaderDiscovery {
	<S> ServiceLoader<S> getServiceLoader(@NonNull Class<S> requiredType);
}
