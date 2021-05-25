package scw.instance;

import scw.core.Constants;
import scw.instance.support.ConfigServiceLoader;
import scw.instance.support.ServiceLoaders;
import scw.instance.support.SpiServiceLoader;
import scw.value.ValueFactory;

public abstract class AbstractServiceLoaderFactory extends AbstractNoArgsInstanceFactoryWrapper
		implements ServiceLoaderFactory {
	protected abstract ValueFactory<String> getConfigFactory();

	protected boolean useSpi(Class<?> serviceClass) {
		return serviceClass.getName().startsWith(Constants.SYSTEM_PACKAGE_NAME);
	}

	public <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		ServiceLoader<S> configServiceLoader = new ConfigServiceLoader<S>(serviceClass, getConfigFactory(), this);
		ServiceLoader<S> spiServiceLoader = null;
		if (useSpi(serviceClass)) {
			spiServiceLoader = new SpiServiceLoader<S>(serviceClass, this);
		}
		return new ServiceLoaders<S>(configServiceLoader, spiServiceLoader);
	}
}
