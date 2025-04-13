package run.soeasy.framework.core;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Provider;
import run.soeasy.framework.core.spi.ProviderFactory;
import run.soeasy.framework.core.type.ResolvableType;

@FunctionalInterface
public interface InstanceFactory extends ProviderFactory {
	@Override
	default <S> Provider<S> getProvider(@NonNull Class<S> requiredType) {
		return getProvider(ResolvableType.forType(requiredType));
	}
	
	<T> Provider<T> getProvider(@NonNull ResolvableType requiredType);
}
