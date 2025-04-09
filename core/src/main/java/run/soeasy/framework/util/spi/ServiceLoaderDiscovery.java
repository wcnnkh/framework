package run.soeasy.framework.util.spi;

import lombok.NonNull;
import run.soeasy.framework.util.collection.Provider;

@FunctionalInterface
public interface ServiceLoaderDiscovery {
	<S> Provider<S> getServiceLoader(@NonNull Class<S> requiredType);
}
