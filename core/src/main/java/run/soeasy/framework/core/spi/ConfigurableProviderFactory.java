package run.soeasy.framework.core.spi;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Provider;

public class ConfigurableProviderFactory extends Services<ProviderFactory> implements ProviderFactory {

	@Override
	public <S> Provider<S> getProvider(@NonNull Class<S> requiredType) {
		for(ProviderFactory factory : this) {
			Provider<S> provider = factory.getProvider(requiredType);
			if(provider != null) {
				return provider;
			}
		}
		return null;
	}

}
