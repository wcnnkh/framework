package run.soeasy.framework.util.spi;

import lombok.NonNull;
import run.soeasy.framework.util.collection.Provider;

@FunctionalInterface
public interface ProviderFactory {
	<S> Provider<S> getProvider(@NonNull Class<S> requiredType);
}
