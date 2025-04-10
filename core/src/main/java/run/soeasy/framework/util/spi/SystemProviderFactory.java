package run.soeasy.framework.util.spi;

import lombok.NonNull;
import run.soeasy.framework.util.collection.Provider;

public final class SystemProviderFactory extends ConfigurableProviderFactory {
	private static volatile SystemProviderFactory instance;

	private SystemProviderFactory() {
	}

	public static SystemProviderFactory getInstance() {
		if (instance == null) {
			synchronized (SystemProviderFactory.class) {
				if (instance == null) {
					instance = new SystemProviderFactory();
					instance.registers(NativeProvider.load(ProviderFactory.class));
				}
			}
		}
		return instance;
	}

	@Override
	public <S> Provider<S> getProvider(@NonNull Class<S> requiredType) {
		Provider<S> provider = super.getProvider(requiredType);
		return provider == null ? NativeProvider.load(requiredType) : provider;
	}
}
