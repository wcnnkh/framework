package run.soeasy.framework.util.spi;

import lombok.NonNull;
import run.soeasy.framework.util.collection.Provider;

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
