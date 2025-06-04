package run.soeasy.framework.core.spi;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Provider;

@FunctionalInterface
public interface ProviderFactory {
	<S> Provider<S> getProvider(@NonNull Class<S> requiredType);
}
