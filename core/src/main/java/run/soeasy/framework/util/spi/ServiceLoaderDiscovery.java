package run.soeasy.framework.util.spi;

import lombok.NonNull;
import run.soeasy.framework.util.collections.ServiceLoader;

@FunctionalInterface
public interface ServiceLoaderDiscovery {
	<S> ServiceLoader<S> getServiceLoader(@NonNull Class<S> requiredType);
}
