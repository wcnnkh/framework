package run.soeasy.framework.util.type;

import lombok.NonNull;
import run.soeasy.framework.util.collection.Provider;
import run.soeasy.framework.util.spi.ProviderFactory;

@FunctionalInterface
public interface InstanceFactory extends ProviderFactory {
	@Override
	default <S> Provider<S> getProvider(@NonNull Class<S> requiredType) {
		return getProvider(ResolvableType.forType(requiredType));
	}
	
	<T> Provider<T> getProvider(@NonNull ResolvableType requiredType);
}
